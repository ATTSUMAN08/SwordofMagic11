plugins {
    alias(libs.plugins.pluginYml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
}

group = "me.attsuman08"
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven(url = "https://repo.md-5.net/content/groups/public/") // LibsDisguises
    maven(url = "https://jitpack.io") // NuVotifier
}

dependencies {
    compileOnly(libs.paperApi)
    compileOnly(libs.protocolLib)
    compileOnly(libs.libsDisguises)
    compileOnly(libs.nuVotifier)

    implementation(libs.kotlinSerializationJson)

    bukkitLibrary(libs.jAsyncMySQL)
    bukkitLibrary(libs.commonsIo)
    bukkitLibrary(libs.commonsCodec)
    bukkitLibrary(libs.gson)
}

bukkit {
    name = project.name
    version = "${project.version}"
    description = "Sword of Magic 11のプラグインのFork"
    authors = listOf("MomiNeko", "ATTSUMAN08")
    apiVersion = "1.20"

    main = "SwordofMagic11.SomCore"
    softDepend = listOf("ProtocolLib", "LibsDisguises", "NuVotifier")

    commands {
        // Developer Commands
        register("SomReload") {
            permission = "som11.reload"
        }
        register("PlayMode") {
            permission = "som11.playMode"
            aliases = listOf("pm")
        }
        register("Test") {
            permission = "som11.dev"
        }
        register("TeleportPlayer") {
            permission = "som11.dev"
            aliases = listOf("tpp")
        }
        register("TeleportWorld") {
            permission = "som11.dev"
            aliases = listOf("tpw")
        }
        register("CreateWorld") {
            permission = "som11.dev"
        }
        register("DeleteWorld") {
            permission = "som11.dev"
        }
        register("LoadWorld") {
            permission = "som11.dev"
        }
        register("UnloadWorld") {
            permission = "som11.dev"
        }
        register("CreateInstance") {
            permission = "som11.dev"
        }
        register("Cast") {
            permission = "som11.dev"
        }
        register("MobClear") {
            permission = "som11.dev"
        }
        register("MobSpawn") {
            permission = "som11.dev"
        }
        register("CreateMaterial") {
            permission = "som11.dev"
            aliases = listOf("cm")
        }
        register("Load") {
            permission = "som11.dev"
        }
        register("Save") {
            permission = "som11.dev"
        }
        register("GetItem") {
            permission = "som11.dev"
        }
        register("BuilderSet") {
            permission = "som11.dev"
            aliases = listOf("brs")
        }
        register("BossBarMessage") {
            permission = "som11.dev"
            aliases = listOf("bbm")
        }
        register("GetMaterial") {
            permission = "som11.dev"
        }
        register("GetCapsule") {
            permission = "som11.dev"
        }
        register("AddEld") {
            permission = "som11.dev"
        }
        register("GetMel") {
            permission = "som11.dev"
        }
        register("GetPet") {
            permission = "som11.dev"
        }
        register("ChangeClass") {
            permission = "som11.dev"
        }
        register("GetSkillPoint") {
            permission = "som11.dev"
        }
        register("GetMemorial") {
            permission = "som11.dev"
        }
        register("DefenseBattleOpenToggle") {
            permission = "som11.dev"
        }
        register("DefenseBattleStart") {
            permission = "som11.dev"
        }
        register("PvPRaidOpenToggle") {
            permission = "som11.dev"
        }
        register("PvPRaidStartToggle") {
            permission = "som11.dev"
        }
        register("ViewMaterialStorage") {
            permission = "som11.dev"
        }
        register("Som10Spawner") {
            permission = "som11.dev"
        }
        register("Som10Mob") {
            permission = "som11.dev"
        }
        register("SetAchievement") {
            permission = "som11.dev"
        }
        register("Clean") {
            permission = "som11.dev"
        }

        // User Commands
        register("Menu") {
            aliases = listOf("m")
        }
        register("Skill") {
            aliases = listOf("s")
        }
        register("Attr") {
            aliases = listOf("a")
        }
        register("BooleanSetting") {
            aliases = listOf("bs")
        }
        register("ValueSetting") {
            aliases = listOf("vs")
        }
        register("Bind")
        register("Trash")
        register("Sit")
        register("PlayerInfo") {
            aliases = listOf("pi")
        }
        register("Party") {
            aliases = listOf("pt")
        }
        register("Trade")
        register("ReqExp")
        register("Gathering") {
            aliases = listOf("g")
        }
        register("TriggerMenu") {
            aliases = listOf("trm")
        }
        register("EldShop")
        register("TotalMemorialStatus")
        register("ResetDPS")
        register("Mania")
        register("SellMaterial")
        register("ViewItem")
        register("Market")
        register("Lock")
        register("SideBarToDo") {
            aliases = listOf("sbtd")
        }
        register("Vote")
        register("Pet")
        register("LightsOut")
        register("PalletStorage") {
            aliases = listOf("ps")
        }
        register("Camera")
        register("Unequip")
        register("BossTimeAttack") {
            aliases = listOf("bta")
        }
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
