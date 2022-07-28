package com.intractable.simm.utils

import com.intractable.simm.R

object Plants {

    // store drawable image references here
    // 0 == purple, 1 == red, 2 == yellow
    // 0 == awake, 1 == asleep, 2 == surprised
    // tulip_red_**_stage0-2 can be used for tulip_**_**_stage 0-2 to reduce app size
    val plantImages = arrayOf(
        arrayOf(  // purple
            intArrayOf(  // awake
                R.drawable.tulip_red_awake_stage0,
                R.drawable.tulip_red_awake_stage1,
                R.drawable.tulip_red_awake_stage2,
                R.drawable.tulip_purple_awake_stage3,
                R.drawable.tulip_purple_awake_stage4,
                R.drawable.tulip_purple_awake_stage5
            ),
            intArrayOf(  // asleep
                R.drawable.tulip_red_asleep_stage0,
                R.drawable.tulip_red_asleep_stage1,
                R.drawable.tulip_red_asleep_stage2,
                R.drawable.tulip_purple_asleep_stage3,
                R.drawable.tulip_purple_asleep_stage4,
                R.drawable.tulip_purple_asleep_stage5
            ),
            intArrayOf(  // surprised
                R.drawable.tulip_red_surprised_stage0,
                R.drawable.tulip_red_surprised_stage1,
                R.drawable.tulip_red_surprised_stage2,
                R.drawable.tulip_purple_surprised_stage3,
                R.drawable.tulip_purple_surprised_stage4,
                R.drawable.tulip_purple_surprised_stage5
            )
        ),
        arrayOf(  //red
            intArrayOf(  // awake
                R.drawable.tulip_red_awake_stage0,
                R.drawable.tulip_red_awake_stage1,
                R.drawable.tulip_red_awake_stage2,
                R.drawable.tulip_red_awake_stage3,
                R.drawable.tulip_red_awake_stage4,
                R.drawable.tulip_red_awake_stage5
            ),
            intArrayOf(  // asleep
                R.drawable.tulip_red_asleep_stage0,
                R.drawable.tulip_red_asleep_stage1,
                R.drawable.tulip_red_asleep_stage2,
                R.drawable.tulip_red_asleep_stage3,
                R.drawable.tulip_red_asleep_stage4,
                R.drawable.tulip_red_asleep_stage5
            ),
            intArrayOf(  // surprised
                R.drawable.tulip_red_surprised_stage0,
                R.drawable.tulip_red_surprised_stage1,
                R.drawable.tulip_red_surprised_stage2,
                R.drawable.tulip_red_surprised_stage3,
                R.drawable.tulip_red_surprised_stage4,
                R.drawable.tulip_red_surprised_stage5
            )
        ),
        arrayOf(  // yellow
            intArrayOf(  // awake
                R.drawable.tulip_red_awake_stage0,
                R.drawable.tulip_red_awake_stage1,
                R.drawable.tulip_red_awake_stage2,
                R.drawable.tulip_yellow_awake_stage3,
                R.drawable.tulip_yellow_awake_stage4,
                R.drawable.tulip_yellow_awake_stage5
            ),
            intArrayOf(  // asleep
                R.drawable.tulip_red_asleep_stage0,
                R.drawable.tulip_red_asleep_stage1,
                R.drawable.tulip_red_asleep_stage2,
                R.drawable.tulip_yellow_asleep_stage3,
                R.drawable.tulip_yellow_asleep_stage4,
                R.drawable.tulip_yellow_asleep_stage5
            ),
            intArrayOf(  // surprised
                R.drawable.tulip_red_surprised_stage0,
                R.drawable.tulip_red_surprised_stage1,
                R.drawable.tulip_red_surprised_stage2,
                R.drawable.tulip_yellow_surprised_stage3,
                R.drawable.tulip_yellow_surprised_stage4,
                R.drawable.tulip_yellow_surprised_stage5
            )
        )
    )
    // The number of sessions to complete for a fully grown plant
    // default is 5, please leave the last entry as 5
    val plantGrowthIntervals: MutableList<Int> = mutableListOf(
        5
    )
}