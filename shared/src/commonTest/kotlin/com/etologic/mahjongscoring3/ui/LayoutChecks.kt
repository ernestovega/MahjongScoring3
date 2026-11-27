package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.max
import kotlin.math.min

/**
 * Finds the layout problems of the screen that a person sees at once: texts that are cut or do not fit, buttons
 * that are not fully on the screen, texts on top of other texts and windows (dialogs) larger than the screen.
 *
 * - A text or a button can be cut only by a scrolling container (a list, a scrolling dialog): the user can scroll
 *   to it. Cut anywhere else, it is a problem.
 * - A text that needs more lines or more width than it gets is a problem. When [allowEllipsis] is true, a text that
 *   ends with "…" is not (MS2 shortens long player and game names the same way).
 * - Two texts of the same window that cover each other are a problem. Only the glyphs count, not the empty space
 *   above and below them (MS2 puts the penalty points 8dp into the box of the points, without touching them). With [onlyInside], only the texts inside
 *   the view with that tag are checked for this (the drawer covers the screen under it).
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.layoutProblems(
    screen: TestScreen,
    allowEllipsis: Boolean = false,
    onlyInside: String? = null,
): List<String> {
    waitForIdle()
    val problems = mutableListOf<String>()
    onAllNodes(isRoot(), useUnmergedTree = true).fetchSemanticsNodes().forEachIndexed { index, root ->
        // The density of the screen: the root of the iOS test window reports 1
        val density = screen.dpi / 160f
        val tolerance = density // 1dp
        fun Rect.dp() = "[${(left / density).toInt()}, ${(top / density).toInt()}, ${(right / density).toInt()}, ${(bottom / density).toInt()}]dp"

        // The window (the app or a dialog) is inside the screen
        val window = Rect(root.positionOnScreen, root.size.let { androidx.compose.ui.geometry.Size(it.width.toFloat(), it.height.toFloat()) })
        val screenRect = Rect(0f, 0f, screen.widthDp * density, screen.heightDp * density)
        val outer = screenRect.inflate(tolerance)
        if (window.left < outer.left || window.top < outer.top || window.right > outer.right || window.bottom > outer.bottom) problems += "window $index ${window.dp()} is larger than the screen ${screenRect.dp()}"

        val texts = mutableListOf<Pair<SemanticsNode, Rect>>()
        root.descendants().forEach { node ->
            // Without the clips of the parents, but with their scales and rotations (graphics layers)
            val coordinates = node.layoutInfo.coordinates
            val unclipped = if (coordinates.isAttached) {
                coordinates.findRootCoordinates().localBoundingBoxOf(coordinates, clipBounds = false)
            } else {
                Rect(node.positionInRoot, androidx.compose.ui.geometry.Size(node.size.width.toFloat(), node.size.height.toFloat()))
            }
            if (unclipped.width <= 0f || unclipped.height <= 0f) return@forEach
            // Views placed out of the window on purpose (a closed drawer) are not seen
            val windowArea = Rect(0f, 0f, window.width, window.height)
            if (!unclipped.overlaps(windowArea)) return@forEach
            val clipped = node.boundsInRoot
            val scrolls = node.ancestors().any { it.isScrollable() }
            val text = node.text()
            val isCut = clipped.width < unclipped.width - tolerance || clipped.height < unclipped.height - tolerance ||
                unclipped.left < -tolerance || unclipped.top < -tolerance ||
                unclipped.right > windowArea.right + tolerance || unclipped.bottom > windowArea.bottom + tolerance
            val name = text?.let { "text \"${it.take(40)}\"" } ?: node.config.getOrNull(SemanticsProperties.TestTag)?.let { "button $it" } ?: "button"

            if ((text != null || node.isClickable()) && isCut && !scrolls) {
                problems += "window $index: $name is cut: ${clipped.dp()} of ${unclipped.dp()}"
            }
            if (text != null) {
                node.textLayout()?.let { layout ->
                    val tooWide = (0 until layout.lineCount).any { layout.getLineRight(it) - layout.getLineLeft(it) > layout.size.width + 1f }
                    val tooHigh = layout.multiParagraph.height > layout.size.height + 1f
                    val overflows = layout.multiParagraph.didExceedMaxLines || tooWide || tooHigh
                    // iOS does not report the ellipsized lines: a text set to end with "…" that overflows is ellipsized
                    val ellipsized = (0 until layout.lineCount).any { layout.isLineEllipsized(it) } ||
                        (layout.layoutInput.overflow == TextOverflow.Ellipsis && overflows)
                    if (ellipsized && !allowEllipsis) problems += "window $index: $name is shortened with an ellipsis"
                    if (overflows && !ellipsized) {
                        problems += "window $index: $name does not fit in ${unclipped.dp()}"
                    }
                }
                if (clipped.width > tolerance && clipped.height > tolerance &&
                    (onlyInside == null || node.ancestors().any { it.config.getOrNull(SemanticsProperties.TestTag) == onlyInside })
                ) texts += node to (node.inkBounds(unclipped)?.intersect(clipped) ?: clipped)
            }
        }
        texts.forEachIndexed { i, (a, aBounds) ->
            texts.drop(i + 1).forEach { (b, bBounds) ->
                if (a.ancestors().contains(b) || b.ancestors().contains(a)) return@forEach
                val overlapWidth = min(aBounds.right, bBounds.right) - max(aBounds.left, bBounds.left)
                val overlapHeight = min(aBounds.bottom, bBounds.bottom) - max(aBounds.top, bBounds.top)
                // The glyph bounds are an estimate (fonts differ): 3dp of margin
                if (overlapWidth > 3 * tolerance && overlapHeight > 3 * tolerance) {
                    problems += "window $index: text \"${a.text()?.take(30)}\" ${aBounds.dp()} covers text \"${b.text()?.take(30)}\" ${bBounds.dp()}"
                }
            }
        }
    }
    return problems
}

private fun SemanticsNode.descendants(): Sequence<SemanticsNode> = sequence {
    children.forEach { child ->
        yield(child)
        yieldAll(child.descendants())
    }
}

private fun SemanticsNode.ancestors(): List<SemanticsNode> = generateSequence(parent) { it.parent }.toList()

private fun SemanticsNode.isScrollable() =
    config.getOrNull(SemanticsActions.ScrollBy) != null ||
        config.getOrNull(SemanticsProperties.VerticalScrollAxisRange) != null ||
        config.getOrNull(SemanticsProperties.HorizontalScrollAxisRange) != null

private fun SemanticsNode.isClickable() = config.getOrNull(SemanticsActions.OnClick) != null

private fun SemanticsNode.text(): String? =
    (config.getOrNull(SemanticsProperties.EditableText)?.text ?: config.getOrNull(SemanticsProperties.Text)?.joinToString(" ") { it.text })
        ?.takeIf { it.isNotBlank() }

private fun SemanticsNode.textLayout(): TextLayoutResult? {
    val results = mutableListOf<TextLayoutResult>()
    config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(results)
    return results.firstOrNull()
}

/**
 * Where the glyphs of the text are drawn, in root coordinates: from the cap height of the first line to the
 * descenders of the last one, and as wide as the widest line, placed by the text alignment.
 */
private fun SemanticsNode.inkBounds(nodeBounds: Rect): Rect? {
    val layout = textLayout() ?: return null
    if (layout.lineCount == 0) return null
    val em = with(layout.layoutInput.density) { layout.layoutInput.style.fontSize.toPx() }
    // The text is as wide as its widest line, placed in the node by its alignment
    val textWidth = minOf(nodeBounds.width, (0 until layout.lineCount).maxOf { layout.getLineRight(it) - layout.getLineLeft(it) })
    val left = when (layout.layoutInput.style.textAlign) {
        TextAlign.Center -> nodeBounds.left + (nodeBounds.width - textWidth) / 2
        TextAlign.End, TextAlign.Right -> nodeBounds.right - textWidth
        else -> nodeBounds.left
    }
    return Rect(
        left = left,
        top = nodeBounds.top + layout.getLineBaseline(0) - 0.72f * em,
        right = left + textWidth,
        bottom = nodeBounds.top + layout.getLineBaseline(layout.lineCount - 1) + 0.2f * em,
    )
}
