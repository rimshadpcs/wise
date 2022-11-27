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
        ),
        arrayOf(  // blue cactus
            intArrayOf(  // awake
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage2,
                R.drawable.cactus_blue_stage3,
                R.drawable.cactus_blue_stage4,
                R.drawable.cactus_blue_stage5
            ),
            intArrayOf(  // awake
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage2,
                R.drawable.cactus_blue_stage3,
                R.drawable.cactus_blue_stage4,
                R.drawable.cactus_blue_stage5
            ),
            intArrayOf(  // awake
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage2,
                R.drawable.cactus_blue_stage3,
                R.drawable.cactus_blue_stage4,
                R.drawable.cactus_blue_stage5
            )
        ),
        arrayOf(  // pink cactus
            intArrayOf(  // awake
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage2,
                R.drawable.cactus_pink_stage3,
                R.drawable.cactus_pink_stage4,
                R.drawable.cactus_pink_stage5
            ),
            intArrayOf(  // awake
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage2,
                R.drawable.cactus_pink_stage3,
                R.drawable.cactus_pink_stage4,
                R.drawable.cactus_pink_stage5
            ),
            intArrayOf(  // awake
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage1,
                R.drawable.cactus_stage2,
                R.drawable.cactus_pink_stage3,
                R.drawable.cactus_pink_stage4,
                R.drawable.cactus_pink_stage5
            )
        )
    )
    // The number of sessions to complete for a fully grown plant
    // default is 5, please leave the last entry as 4
    val plantGrowthIntervals: MutableList<Int> = mutableListOf(
        1, 2, 2, 2, 3, 3, 3, 4
    )

    // dex drawables
    val dexIcons = intArrayOf(
        R.drawable.dex_tulip_purple,
        R.drawable.dex_tulip_red,
        R.drawable.dex_tulip_yellow,
        R.drawable.dex_cactus_blue,
        R.drawable.dex_cactus_pink
    )

    val dexSilhouettes = intArrayOf(
        R.drawable.dex_tulip_silhouette,
        R.drawable.dex_tulip_silhouette,
        R.drawable.dex_tulip_silhouette,
        R.drawable.dex_cactus_silhouette,
        R.drawable.dex_cactus_silhouette
    )

    val plantAnimations = arrayOf(
        arrayOf(  // purple tulip
            "alltulipstage_one",
            "purpletulipstage_two",
            "purpletulipstage_three",
            "purpletulipstage_four"
        ),
        arrayOf(  // red tulip
            "alltulipstage_one",
            "redtulipstage_two",
            "redtulipstage_three",
            "redtulipstage_four"
        ),
        arrayOf(  // yellow tulip
            "alltulipstage_one",
            "yellowtulipstage_two",
            "yellowtulipstage_three",
            "yellowtulipstage_four"
        ),
        arrayOf(  // blue cactus
            "alltulipstage_one",
            "redtulipstage_two",
            "redtulipstage_three",
            "redtulipstage_four"
        ),
        arrayOf(  // pink cactus
            "alltulipstage_one",
            "yellowtulipstage_two",
            "yellowtulipstage_three",
            "yellowtulipstage_four"
        )
    )

    val prePlantAnimations = arrayOf(
        arrayOf(  // purple tulip
            "tulip_s1_start",
            "tulip_s2_start",
            "tulip_purple_s3_start",
            "tulip_purple_s4_start"
        ),
        arrayOf(  // red tulip
            "tulip_s1_start",
            "tulip_s2_start",
            "tulip_red_s3_start",
            "tulip_red_s4_start"
        ),
        arrayOf(  // yellow tulip
            "tulip_s1_start",
            "tulip_s2_start",
            "tulip_yellow_s3_start",
            "tulip_yellow_s4_start"
        ),
        arrayOf(  // blue cactus
            "cactus_s1_start",
            "cactus_s2_start",
            "cactus_blue_s3_start",
            "cactus_blue_s4_start"
        ),
        arrayOf(  // pink cactus
            "cactus_s1_start",
            "cactus_s2_start",
            "cactus_pink_s3_start",
            "cactus_pink_s4_start"
        )
    )
    val postPlantAnimations = arrayOf(
        arrayOf(  // purple tulip
            "tulip_s2_end",
            "tulip_purple_s3_end",
            "tulip_purple_s4_end",
            "tulip_purple_s5_end"
        ),
        arrayOf(  // red tulip
            "tulip_s2_end",
            "tulip_red_s3_end",
            "tulip_red_s4_end",
            "tulip_red_s5_end"
        ),
        arrayOf(  // yellow tulip
            "tulip_s2_end",
            "tulip_yellow_s3_end",
            "tulip_yellow_s4_end",
            "tulip_yellow_s5_end"
        ),
        arrayOf(  // blue cactus
            "cactus_s2_end",
            "cactus_blue_s3_end",
            "cactus_blue_s4_end",
            "cactus_blue_s5_end"
        ),
        arrayOf(  // pink cactus
            "cactus_s2_end",
            "cactus_pink_s3_end",
            "cactus_pink_s4_end",
            "cactus_pink_s5_end"
        )
    )

    val plantNames = arrayOf(
        "Purple Tulip",
        "Red Tulip",
        "Yellow Tulip",
        "Blue Cactus",
        "Pink Cactus"
    )
}