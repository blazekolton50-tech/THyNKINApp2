package com.patsy.app.pawmoji

enum class PawMojiCategory { REACTION, LOVE, DAILY, HOBBY, ADVENTURE, SEASONAL, WORK, FOOD, CELEBRATION, SPORT, STYLE }

data class PawMojiDefinition(
    val id: String,
    val label: String,
    val category: PawMojiCategory,
    val approvedLocked: Boolean = false,
    val assetName: String? = null,
    val transparentSingleAsset: Boolean = true
)

/** Canonical runtime registry: ONE entry = ONE individual PawMoji keyboard item. */
object PawMojiCatalog {
    val items = listOf(
        PawMojiDefinition("happy", "Happy", PawMojiCategory.REACTION, true),
        PawMojiDefinition("love", "Love", PawMojiCategory.LOVE, true),
        PawMojiDefinition("excited", "Excited", PawMojiCategory.REACTION, true),
        PawMojiDefinition("sad", "Sad", PawMojiCategory.REACTION, true),
        PawMojiDefinition("angry", "Angry", PawMojiCategory.REACTION, true),
        PawMojiDefinition("wink_hello", "Wink / Hello", PawMojiCategory.REACTION, true),
        PawMojiDefinition("sleepy", "Sleepy", PawMojiCategory.DAILY, true),
        PawMojiDefinition("confused_thinking", "Confused / Thinking", PawMojiCategory.REACTION, true),
        PawMojiDefinition("big_love", "Big Love", PawMojiCategory.LOVE, true),
        PawMojiDefinition("happy_waving", "Happy Waving", PawMojiCategory.REACTION, true),
        PawMojiDefinition("bath_bubbles", "Bath / Bubbles", PawMojiCategory.DAILY, true),
        PawMojiDefinition("coffee_first", "Coffee First", PawMojiCategory.FOOD, true),
        PawMojiDefinition("adventure_travel", "Adventure / Travel", PawMojiCategory.ADVENTURE, true),
        PawMojiDefinition("chill_movie", "Chill / Movie Night", PawMojiCategory.DAILY, true),
        PawMojiDefinition("play_fetch", "Play / Fetch", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("gamer", "Gamer Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("judgemental", "Judgemental Patsy", PawMojiCategory.REACTION, true),
        PawMojiDefinition("race_car_red", "Race Car Patsy", PawMojiCategory.SPORT, true),
        PawMojiDefinition("biker", "Biker Patsy", PawMojiCategory.ADVENTURE, true),
        PawMojiDefinition("bored", "Bored Patsy", PawMojiCategory.REACTION, true),
        PawMojiDefinition("spa", "Spa Patsy", PawMojiCategory.DAILY, true),
        PawMojiDefinition("pop_star", "Pop Star Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("photography", "Photography Patsy", PawMojiCategory.HOBBY, true),
        PawMojiDefinition("pilot", "Pilot Patsy", PawMojiCategory.ADVENTURE, true),
        PawMojiDefinition("munchies", "Munchies Patsy", PawMojiCategory.FOOD, true),

        PawMojiDefinition("laughing", "Laughing", PawMojiCategory.REACTION),
        PawMojiDefinition("cry_laughing", "Cry Laughing", PawMojiCategory.REACTION),
        PawMojiDefinition("heart_eyes", "Heart Eyes", PawMojiCategory.LOVE),
        PawMojiDefinition("kiss", "Kiss", PawMojiCategory.LOVE),
        PawMojiDefinition("hug", "Hug", PawMojiCategory.LOVE),
        PawMojiDefinition("shy", "Shy", PawMojiCategory.REACTION),
        PawMojiDefinition("surprised", "Surprised", PawMojiCategory.REACTION),
        PawMojiDefinition("shocked", "Shocked", PawMojiCategory.REACTION),
        PawMojiDefinition("eye_roll", "Eye Roll", PawMojiCategory.REACTION),
        PawMojiDefinition("side_eye", "Side Eye", PawMojiCategory.REACTION),
        PawMojiDefinition("smug", "Smug", PawMojiCategory.REACTION),
        PawMojiDefinition("cool", "Cool", PawMojiCategory.REACTION),
        PawMojiDefinition("crying", "Crying", PawMojiCategory.REACTION),
        PawMojiDefinition("anxious", "Anxious", PawMojiCategory.REACTION),
        PawMojiDefinition("stressed", "Stressed", PawMojiCategory.REACTION),
        PawMojiDefinition("facepalm", "Facepalm", PawMojiCategory.REACTION),
        PawMojiDefinition("nope", "Nope", PawMojiCategory.REACTION),
        PawMojiDefinition("yes", "Yes!", PawMojiCategory.REACTION),
        PawMojiDefinition("please", "Please", PawMojiCategory.REACTION),
        PawMojiDefinition("thank_you", "Thank You", PawMojiCategory.REACTION),
        PawMojiDefinition("goodbye", "Goodbye / Bye", PawMojiCategory.REACTION),
        PawMojiDefinition("sick", "Sick", PawMojiCategory.DAILY),
        PawMojiDefinition("relaxed", "Relaxed", PawMojiCategory.DAILY),
        PawMojiDefinition("rainy_day", "Rainy Day", PawMojiCategory.DAILY),
        PawMojiDefinition("cold_weather", "Cold Weather", PawMojiCategory.DAILY),
        PawMojiDefinition("bedtime", "Bedtime", PawMojiCategory.DAILY),
        PawMojiDefinition("morning", "Morning", PawMojiCategory.DAILY),
        PawMojiDefinition("workout", "Workout", PawMojiCategory.SPORT),
        PawMojiDefinition("football", "Football", PawMojiCategory.SPORT),
        PawMojiDefinition("cheerleader", "Cheerleader", PawMojiCategory.SPORT),
        PawMojiDefinition("beach_day", "Beach Day", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("pool_time", "Pool Time", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("camping", "Camping", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("fishing", "Fishing", PawMojiCategory.HOBBY),
        PawMojiDefinition("gardening", "Gardening", PawMojiCategory.HOBBY),
        PawMojiDefinition("tourist", "Tourist", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("classic_aviator", "Classic Aviator", PawMojiCategory.ADVENTURE),
        PawMojiDefinition("office_work", "Office / Work", PawMojiCategory.WORK),
        PawMojiDefinition("detective", "Detective", PawMojiCategory.WORK),
        PawMojiDefinition("chef_cooking", "Chef / Cooking", PawMojiCategory.FOOD),
        PawMojiDefinition("foodie", "Foodie", PawMojiCategory.FOOD),
        PawMojiDefinition("ice_cream", "Ice Cream", PawMojiCategory.FOOD),
        PawMojiDefinition("pizza", "Pizza Time", PawMojiCategory.FOOD),
        PawMojiDefinition("donuts", "Donuts", PawMojiCategory.FOOD),
        PawMojiDefinition("birthday", "Birthday", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("party", "Party", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("celebrate", "Celebrate", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("halloween", "Halloween", PawMojiCategory.SEASONAL),
        PawMojiDefinition("christmas_santa", "Christmas / Santa", PawMojiCategory.SEASONAL),
        PawMojiDefinition("angel", "Angel", PawMojiCategory.SEASONAL),
        PawMojiDefinition("rainbow", "Rainbow", PawMojiCategory.CELEBRATION),
        PawMojiDefinition("music_lover", "Music Lover", PawMojiCategory.HOBBY),
        PawMojiDefinition("rock_star", "Rock Star", PawMojiCategory.HOBBY),
        PawMojiDefinition("artist", "Artist", PawMojiCategory.HOBBY),
        PawMojiDefinition("reader", "Book / Reading", PawMojiCategory.HOBBY),
        PawMojiDefinition("student", "Student", PawMojiCategory.WORK),
        PawMojiDefinition("scientist", "Scientist", PawMojiCategory.WORK),
        PawMojiDefinition("doctor", "Doctor", PawMojiCategory.WORK),
        PawMojiDefinition("builder", "Builder", PawMojiCategory.WORK),
        PawMojiDefinition("royal", "Royal", PawMojiCategory.STYLE),
        PawMojiDefinition("glam", "Glam", PawMojiCategory.STYLE),
        PawMojiDefinition("superhero", "Superhero", PawMojiCategory.STYLE)
    )

    init {
        require(items.map { it.id }.distinct().size == items.size) { "Duplicate PawMoji IDs are not allowed" }
    }

    val byId = items.associateBy { it.id }
    val approvedLocked = items.filter { it.approvedLocked }
    val awaitingAsset = items.filterNot { it.approvedLocked }
}
