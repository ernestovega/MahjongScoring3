package screens.help.fan.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.all_chows
import mahjongscoring3.composeapp.generated.resources.all_even_pungs
import mahjongscoring3.composeapp.generated.resources.all_fives
import mahjongscoring3.composeapp.generated.resources.all_green
import mahjongscoring3.composeapp.generated.resources.all_honors
import mahjongscoring3.composeapp.generated.resources.all_pungs
import mahjongscoring3.composeapp.generated.resources.all_simples
import mahjongscoring3.composeapp.generated.resources.all_terminals
import mahjongscoring3.composeapp.generated.resources.all_terminals_and_honors
import mahjongscoring3.composeapp.generated.resources.all_types
import mahjongscoring3.composeapp.generated.resources.big_four_winds
import mahjongscoring3.composeapp.generated.resources.big_three_dragons
import mahjongscoring3.composeapp.generated.resources.big_three_winds
import mahjongscoring3.composeapp.generated.resources.chicken_hand
import mahjongscoring3.composeapp.generated.resources.closed_wait
import mahjongscoring3.composeapp.generated.resources.concealed_hand
import mahjongscoring3.composeapp.generated.resources.concealed_kong
import mahjongscoring3.composeapp.generated.resources.description_closed_wait
import mahjongscoring3.composeapp.generated.resources.description_concealed_hand
import mahjongscoring3.composeapp.generated.resources.description_edge_wait
import mahjongscoring3.composeapp.generated.resources.description_fully_concealed_hand
import mahjongscoring3.composeapp.generated.resources.description_last_tile
import mahjongscoring3.composeapp.generated.resources.description_last_tile_claim
import mahjongscoring3.composeapp.generated.resources.description_last_tile_draw
import mahjongscoring3.composeapp.generated.resources.description_melded_hand
import mahjongscoring3.composeapp.generated.resources.description_out_with_replacement_tile
import mahjongscoring3.composeapp.generated.resources.description_robbing_the_kong
import mahjongscoring3.composeapp.generated.resources.description_self_drawn
import mahjongscoring3.composeapp.generated.resources.description_single_waiting
import mahjongscoring3.composeapp.generated.resources.double_pungs
import mahjongscoring3.composeapp.generated.resources.dragon_pung
import mahjongscoring3.composeapp.generated.resources.edge_wait
import mahjongscoring3.composeapp.generated.resources.flower_tiles
import mahjongscoring3.composeapp.generated.resources.four_concealed_pungs
import mahjongscoring3.composeapp.generated.resources.four_kongs
import mahjongscoring3.composeapp.generated.resources.four_pure_shifted_chows
import mahjongscoring3.composeapp.generated.resources.four_pure_shifted_pungs
import mahjongscoring3.composeapp.generated.resources.full_flush
import mahjongscoring3.composeapp.generated.resources.fully_concealed_hand
import mahjongscoring3.composeapp.generated.resources.greater_honors_and_knitted_tiles
import mahjongscoring3.composeapp.generated.resources.half_flush
import mahjongscoring3.composeapp.generated.resources.knitted_straight
import mahjongscoring3.composeapp.generated.resources.last_tile
import mahjongscoring3.composeapp.generated.resources.last_tile_claim
import mahjongscoring3.composeapp.generated.resources.last_tile_draw
import mahjongscoring3.composeapp.generated.resources.lesser_honors_and_knitted_tiles
import mahjongscoring3.composeapp.generated.resources.little_four_winds
import mahjongscoring3.composeapp.generated.resources.little_three_dragons
import mahjongscoring3.composeapp.generated.resources.lower_four
import mahjongscoring3.composeapp.generated.resources.lower_tiles
import mahjongscoring3.composeapp.generated.resources.medium_tiles
import mahjongscoring3.composeapp.generated.resources.melded_hand
import mahjongscoring3.composeapp.generated.resources.melded_kong
import mahjongscoring3.composeapp.generated.resources.mixed_double_chow
import mahjongscoring3.composeapp.generated.resources.mixed_shifted_chows
import mahjongscoring3.composeapp.generated.resources.mixed_shifted_pungs
import mahjongscoring3.composeapp.generated.resources.mixed_straight
import mahjongscoring3.composeapp.generated.resources.mixed_triple_chow
import mahjongscoring3.composeapp.generated.resources.nine_gates
import mahjongscoring3.composeapp.generated.resources.no_honors
import mahjongscoring3.composeapp.generated.resources.one_voided_suit
import mahjongscoring3.composeapp.generated.resources.out_with_replacement_tile
import mahjongscoring3.composeapp.generated.resources.outside_hand
import mahjongscoring3.composeapp.generated.resources.prevalent_wind
import mahjongscoring3.composeapp.generated.resources.pung_of_terminals_or_honors
import mahjongscoring3.composeapp.generated.resources.pure_double_chow
import mahjongscoring3.composeapp.generated.resources.pure_shifted_chows
import mahjongscoring3.composeapp.generated.resources.pure_shifted_pungs
import mahjongscoring3.composeapp.generated.resources.pure_straight
import mahjongscoring3.composeapp.generated.resources.pure_terminal_chows
import mahjongscoring3.composeapp.generated.resources.pure_triple_chow
import mahjongscoring3.composeapp.generated.resources.quadruple_chow
import mahjongscoring3.composeapp.generated.resources.reversible_tiles
import mahjongscoring3.composeapp.generated.resources.robbing_the_kong
import mahjongscoring3.composeapp.generated.resources.seat_wind
import mahjongscoring3.composeapp.generated.resources.self_drawn
import mahjongscoring3.composeapp.generated.resources.seven_pairs
import mahjongscoring3.composeapp.generated.resources.seven_shifted_pairs
import mahjongscoring3.composeapp.generated.resources.short_straight
import mahjongscoring3.composeapp.generated.resources.single_waiting
import mahjongscoring3.composeapp.generated.resources.thirteen_orphans
import mahjongscoring3.composeapp.generated.resources.three_concealed_pungs
import mahjongscoring3.composeapp.generated.resources.three_kongs
import mahjongscoring3.composeapp.generated.resources.three_suited_terminal_chows
import mahjongscoring3.composeapp.generated.resources.tile_hog
import mahjongscoring3.composeapp.generated.resources.triple_pungs
import mahjongscoring3.composeapp.generated.resources.two_concealed_kongs
import mahjongscoring3.composeapp.generated.resources.two_concealed_pungs
import mahjongscoring3.composeapp.generated.resources.two_dragon_pungs
import mahjongscoring3.composeapp.generated.resources.two_melded_kongs
import mahjongscoring3.composeapp.generated.resources.two_terminal_chows
import mahjongscoring3.composeapp.generated.resources.upper_four
import mahjongscoring3.composeapp.generated.resources.upper_tiles
import screens.help.fan.model.Fan

class DefaultFanRepository : FanRepository {

    override val getFanFlow: Flow<List<Fan>> =
//        with(getLocalizedContext()) {
        flowOf(
            listOf(
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.pure_double_chow,
                    //fanImage = R.drawable.pure_double_chow
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.mixed_double_chow,
                    //fanImage = R.drawable.mixed_double_chow
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.short_straight,
                    //fanImage = R.drawable.short_straight
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.two_terminal_chows,
                    //fanImage = R.drawable.two_terminal_chows
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.pung_of_terminals_or_honors,
                    //fanImage = R.drawable.pung_of_terminal_or_honors
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.melded_kong,
                    //fanImage = R.drawable.melded_kong
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.one_voided_suit,
                    //fanImage = R.drawable.one_voided_suit
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.no_honors,
                    //fanImage = R.drawable.no_honors
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.edge_wait,
                    fanDescription = Res.string.description_edge_wait,
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.closed_wait,
                    fanDescription = Res.string.description_closed_wait,
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.single_waiting,
                    fanDescription = Res.string.description_single_waiting,
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.self_drawn,
                    fanDescription = Res.string.description_self_drawn,
                ),
                Fan(
                    fanPoints = 1,
                    fanName = Res.string.flower_tiles,
                    //fanImage = R.drawable.flower_tiles
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.dragon_pung,
                    //fanImage = R.drawable.dragon_pung
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.prevalent_wind,
                    //fanImage = R.drawable.prevalent_wind
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.seat_wind,
                    //fanImage = R.drawable.seat_wind
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.concealed_hand,
                    fanDescription = Res.string.description_concealed_hand,
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.all_chows,
                    //fanImage = R.drawable.all_chows
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.tile_hog,
                    //fanImage = R.drawable.tile_hog
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.double_pungs,
                    //fanImage = R.drawable.double_pungs
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.two_concealed_pungs,
                    //fanImage = R.drawable.two_concealed_pungs
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.concealed_kong,
                    //fanImage = R.drawable.concealed_kong
                ),
                Fan(
                    fanPoints = 2,
                    fanName = Res.string.all_simples,
                    //fanImage = R.drawable.all_simples
                ),
                Fan(
                    fanPoints = 4,
                    fanName = Res.string.outside_hand,
                    //fanImage = R.drawable.outside_hand
                ),
                Fan(
                    fanPoints = 4,
                    fanName = Res.string.fully_concealed_hand,
                    fanDescription = Res.string.description_fully_concealed_hand,
                ),
                Fan(
                    fanPoints = 4,
                    fanName = Res.string.two_melded_kongs,
                    //fanImage = R.drawable.two_melded_kongs
                ),
                Fan(
                    fanPoints = 4,
                    fanName = Res.string.last_tile,
                    fanDescription = Res.string.description_last_tile,
                ),
                Fan(
                    fanPoints = 6,
                    fanName = Res.string.all_pungs,
                    //fanImage = R.drawable.all_pungs
                ),
                Fan(
                    fanPoints = 6,
                    fanName = Res.string.half_flush,
                    //fanImage = R.drawable.half_flush
                ),
                Fan(
                    fanPoints = 6,
                    fanName = Res.string.mixed_shifted_chows,
                    //fanImage = R.drawable.mixed_shifted_chows
                ),
                Fan(
                    fanPoints = 6,
                    fanName = Res.string.all_types,
                    //fanImage = R.drawable.all_types
                ),
                Fan(
                    fanPoints = 6,
                    fanName = Res.string.melded_hand,
                    fanDescription = Res.string.description_melded_hand,
                ),
                Fan(
                    fanPoints = 6,
                    fanName = Res.string.two_dragon_pungs,
                    //fanImage = R.drawable.two_dragon_pungs
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.mixed_straight,
                    //fanImage = R.drawable.mixed_straight
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.reversible_tiles,
                    //fanImage = R.drawable.reversible_tiles
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.mixed_triple_chow,
                    //fanImage = R.drawable.mixed_triple_chow
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.mixed_shifted_pungs,
                    //fanImage = R.drawable.mixed_shifted_pungs
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.chicken_hand,
                    //fanImage = R.drawable.chicken_hand
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.last_tile_draw,
                    fanDescription = Res.string.description_last_tile_draw,
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.last_tile_claim,
                    fanDescription = Res.string.description_last_tile_claim,
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.out_with_replacement_tile,
                    fanDescription = Res.string.description_out_with_replacement_tile,
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.robbing_the_kong,
                    fanDescription = Res.string.description_robbing_the_kong
                ),
                Fan(
                    fanPoints = 8,
                    fanName = Res.string.two_concealed_kongs,
                    //fanImage = R.drawable.two_concealed_kongs
                ),
                Fan(
                    fanPoints = 12,
                    fanName = Res.string.lesser_honors_and_knitted_tiles,
                    //fanImage = R.drawable.lesser_honors_and_knitted_tiles
                ),
                Fan(
                    fanPoints = 12,
                    fanName = Res.string.knitted_straight,
                    //fanImage = R.drawable.knitted_straight
                ),
                Fan(
                    fanPoints = 12,
                    fanName = Res.string.upper_four,
                    //fanImage = R.drawable.upper_four
                ),
                Fan(
                    fanPoints = 12,
                    fanName = Res.string.lower_four,
                    //fanImage = R.drawable.lower_four
                ),
                Fan(
                    fanPoints = 12,
                    fanName = Res.string.big_three_winds,
                    //fanImage = R.drawable.big_three_winds
                ),
                Fan(
                    fanPoints = 16,
                    fanName = Res.string.pure_straight,
                    //fanImage = R.drawable.pure_straight
                ),
                Fan(
                    fanPoints = 16,
                    fanName = Res.string.three_suited_terminal_chows,
                    //fanImage = R.drawable.three_suited_terminal_chows
                ),
                Fan(
                    fanPoints = 16,
                    fanName = Res.string.pure_shifted_chows,
                    //fanImage = R.drawable.pure_shifted_chows
                ),
                Fan(
                    fanPoints = 16,
                    fanName = Res.string.all_fives,
                    //fanImage = R.drawable.all_fives
                ),
                Fan(
                    fanPoints = 16,
                    fanName = Res.string.triple_pungs,
                    //fanImage = R.drawable.triple_pungs
                ),
                Fan(
                    fanPoints = 16,
                    fanName = Res.string.three_concealed_pungs,
                    //fanImage = R.drawable.three_concealed_pungs
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.seven_pairs,
                    //fanImage = R.drawable.seven_pairs
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.greater_honors_and_knitted_tiles,
                    //fanImage = R.drawable.greater_honors_and_knitted_tiles
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.all_even_pungs,
                    //fanImage = R.drawable.all_even_pungs
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.full_flush,
                    //fanImage = R.drawable.full_flush
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.pure_triple_chow,
                    //fanImage = R.drawable.pure_triple_chow
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.pure_shifted_pungs,
                    //fanImage = R.drawable.pure_shifted_pungs
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.upper_tiles,
                    //fanImage = R.drawable.upper_tiles
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.medium_tiles,
                    //fanImage = R.drawable.medium_tiles
                ),
                Fan(
                    fanPoints = 24,
                    fanName = Res.string.lower_tiles,
                    //fanImage = R.drawable.lower_tiles
                ),
                Fan(
                    fanPoints = 32,
                    fanName = Res.string.four_pure_shifted_chows,
                    //fanImage = R.drawable.four_pure_shifted_chows
                ),
                Fan(
                    fanPoints = 32,
                    fanName = Res.string.three_kongs,
                    //fanImage = R.drawable.three_kongs
                ),
                Fan(
                    fanPoints = 32,
                    fanName = Res.string.all_terminals_and_honors,
                    //fanImage = R.drawable.all_terminals_and_honors
                ),
                Fan(
                    fanPoints = 48,
                    fanName = Res.string.quadruple_chow,
                    //fanImage = R.drawable.quadruple_chow
                ),
                Fan(
                    fanPoints = 48,
                    fanName = Res.string.four_pure_shifted_pungs,
                    //fanImage = R.drawable.four_pure_shifted_pungs
                ),
                Fan(
                    fanPoints = 64,
                    fanName = Res.string.all_terminals,
                    //fanImage = R.drawable.all_terminals
                ),
                Fan(
                    fanPoints = 64,
                    fanName = Res.string.all_honors,
                    //fanImage = R.drawable.all_honors
                ),
                Fan(
                    fanPoints = 64,
                    fanName = Res.string.little_four_winds,
                    //fanImage = R.drawable.little_four_winds
                ),
                Fan(
                    fanPoints = 64,
                    fanName = Res.string.little_three_dragons,
                    //fanImage = R.drawable.little_three_dragons
                ),
                Fan(
                    fanPoints = 64,
                    fanName = Res.string.four_concealed_pungs,
                    //fanImage = R.drawable.four_concealed_pungs
                ),
                Fan(
                    fanPoints = 64,
                    fanName = Res.string.pure_terminal_chows,
                    //fanImage = R.drawable.pure_terminal_chows
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.thirteen_orphans,
                    //fanImage = R.drawable.thirteen_orphans
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.big_three_dragons,
                    //fanImage = R.drawable.big_three_dragons
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.big_four_winds,
                    //fanImage = R.drawable.big_four_winds
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.all_green,
                    //fanImage = R.drawable.all_green
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.nine_gates,
                    //fanImage = R.drawable.nine_gates
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.four_kongs,
                    //fanImage = R.drawable.four_kongs
                ),
                Fan(
                    fanPoints = 88,
                    fanName = Res.string.seven_shifted_pairs,
                    //fanImage = R.drawable.seven_shifted_pairs
                ),
            )
        )
//        }
}