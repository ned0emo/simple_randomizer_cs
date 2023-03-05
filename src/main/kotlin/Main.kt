import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong
import kotlin.random.Random

const val pathToRndata = "./rndata"
const val pathToWeapons = "./rndata/basedata/configs/weapons"
const val pathToMisc = "./rndata/basedata/configs/misc"
const val pathToGameplay = "./rndata/basedata/configs/gameplay"
const val pathToText = "./rndata/basedata/configs/text"

const val sourceLink = "___"

fun main(args: Array<String>) {
    println(
        "WelCUM в программу для рандомизации некоторой дичи в ЧН\n" +
                "К сожалению, разрабы сталкера испоганили игру скриптами, потому смены группировок " +
                "и озвучки НПС, в отличие от рандомайзера ТЧ, тут не будет\n" +
                "Да, без графического интерфейса, потому что теперь я линуксоид без нормального C#\n\n" +
                "Для выбора действия напиши одну из указанных цифр в консоль и нажми Enter\n\n" +
                "Ссыль на исходный код: $sourceLink\n" +
                "Разработчик: ned0emo\n" +
                "Версия 1.0\n" +
                "-----------------------------------------------------------------\n" +
                "2 - сгенерировать всё\n1 - выбрать, что генерировать\n0 - выход: "
    )

    var answer: String?
    do {
        answer = readlnOrNull()
    } while (answer != "1" && answer != "0" && answer != "2")
    if (answer == "0") {
        return
    }

    val errorMap = mutableMapOf<String, String>()

    val artefactsFile = File(pathToRndata, "af.txt")
    val artefacts = if (artefactsFile.canRead()) artefactsFile.readLines() else {
        errorMap["artefacts.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }
    val ammosFile = File(pathToRndata, "ammos.txt")
    val ammos = if (ammosFile.canRead()) ammosFile.readLines() else {
        errorMap["ammos.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }
    val devicesFile = File(pathToRndata, "devices.txt")
    val devices = if (devicesFile.canRead()) devicesFile.readLines() else {
        errorMap["devices.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }
    val itemsFile = File(pathToRndata, "items.txt")
    val items = if (itemsFile.canRead()) itemsFile.readLines() else {
        errorMap["items.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }
    val outfitFile = File(pathToRndata, "outfit.txt")
    val outfits = if (outfitFile.canRead()) outfitFile.readLines() else {
        errorMap["outfits.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }
    val pistolsFile = File(pathToRndata, "pistols.txt")
    val pistols = if (pistolsFile.canRead()) pistolsFile.readLines() else {
        errorMap["pistols.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }
    val riflesFile = File(pathToRndata, "rifles.txt")
    val rifles = if (riflesFile.canRead()) riflesFile.readLines() else {
        errorMap["rifles.txt"] = "Ошибка чтения файла или файл пустой"
        listOf()
    }

    val gamedata = File("./gamedata${SimpleDateFormat("dd.MM.yyyy_hh.mm.ss").format(Date())}")
    if (gamedata.mkdir()) {
        answer = getUserInput(answer, "Изменять экипировку НПС? 1 - да, 0 - нет: ")

        if (answer != "0") {
            try {
                errorMap.putAll(generateNpc(rifles, pistols, gamedata))
            } catch (e: Exception) {
                println(
                    "Ошибка обработки НПС. Операция прервана\n" +
                            "-----------------------------------------------------------------"
                )
                printLogs(errorMap, e)
                return
            }
        }

        answer = getUserInput(answer, "Изменять статы оружия? 1 - да, 0 - нет: ")

        if (answer != "0") {
            try {
                errorMap.putAll(generateWeapons(gamedata))
            } catch (e: Exception) {
                println(
                    "Ошибка обработки оружия. Операция прервана\n" +
                            "-----------------------------------------------------------------"
                )
                printLogs(errorMap, e)
                return
            }
        }

        answer = getUserInput(answer, "Изменять статы артефактов? 1 - да, 0 - нет: ")

        if (answer != "0") {
            try {
                errorMap.putAll(generateArtefacts(gamedata))
            } catch (e: Exception) {
                println(
                    "Ошибка обработки артефактов. Операция прервана\n" +
                            "-----------------------------------------------------------------"
                )
                printLogs(errorMap, e)
                return
            }
        }

        answer = getUserInput(answer, "Изменять статы брони? 1 - да, 0 - нет: ")

        if (answer != "0") {
            try {
                errorMap.putAll(generateOutfits(gamedata))
            } catch (e: Exception) {
                println(
                    "Ошибка обработки брони. Операция прервана\n" +
                            "-----------------------------------------------------------------"
                )
                printLogs(errorMap, e)
                return
            }
        }

        answer = getUserInput(answer, "Изменять содержимое тайников? 1 - да, 0 - нет: ")

        if (answer != "0") {
            try {
                errorMap.putAll(generateStashes(gamedata, rifles, pistols, items, outfits, devices, artefacts, ammos))
            } catch (e: Exception) {
                println(
                    "Ошибка обработки тайников. Операция прервана\n" +
                            "-----------------------------------------------------------------"
                )
                printLogs(errorMap, e)
                return
            }
        }

        answer = getUserInput(answer, "Применить кривой перевод? 1 - да, 0 - нет: ")

        if (answer != "0") {
            try {
                errorMap.putAll(copyTranslate(gamedata))
            } catch (e: Exception) {
                println(
                    "Ошибка обработки косячного перевода. Операция прервана\n" +
                            "-----------------------------------------------------------------"
                )
                printLogs(errorMap, e)
                return
            }
        }

        println("\nOK\n")
        if (errorMap.isNotEmpty()) {
            println("-----------------------------------------------------------------")
            println("При работе программы возникли следующие ошибки:\n")
            printLogs(errorMap)
            return
        }
    } else {
        println("Ошибка создания директории ${gamedata.path}")
    }

    println("Для продолжения нажмите Enter...")
    readlnOrNull()
}

fun generateNpc(rifles: List<String>, pistols: List<String>, gamedata: File): MutableMap<String, String> {
    val gameplayDir = File(pathToGameplay)
    val errorMap = mutableMapOf<String, String>()

    val exportDir = File(gamedata, "configs/gameplay")
    if (!exportDir.mkdirs()) {
        errorMap[exportDir.path] = "Ошибка создания директории"
        return errorMap
    }

    val exceptionsFile = File(pathToRndata, "exceptions.txt")
    val exceptions = if (exceptionsFile.canRead()) exceptionsFile.readLines() else {
        errorMap[exceptionsFile.path] = "Ошибка чтения файла или файл пустой"
        listOf()
    }

    val modelsFile = File(pathToRndata, "models.txt")
    val models = if (modelsFile.canRead()) modelsFile.readLines() else {
        errorMap[modelsFile.path] = "Ошибка чтения файла или файл пустой"
        listOf()
    }

    for (file in gameplayDir.listFiles()!!) {
        if (file.canRead() && file.isFile) {
            val charDesc = file.readText().split("</specific_character>")
            var newCharDesc = ""
            for (character in charDesc.dropLast(1)) {
                var skip = false

                for (exception in exceptions) {
                    if (character.contains("\"$exception\"")) {
                        skip = true
                        break
                    }
                }

                if (skip) {
                    newCharDesc += "$character</specific_character>"
                    continue
                }

                var newCharacter = ""

                //Модель
                if (models.isNotEmpty()) {
                    if (character.contains("<visual>")) {
                        val currentModel =
                            character.substring(character.indexOf("<visual>") + 8, character.indexOf("</visual>"))
                        val newModel = "actors\\" + models.random()
                        newCharacter = character.replace(">$currentModel<", ">$newModel<")
                    }
                }

                //Супплаи
                if (rifles.isNotEmpty() && pistols.isNotEmpty()) {
                    if (newCharacter.contains("<supplies>")) {
                        val suppliesStr =
                            newCharacter.substring(
                                newCharacter.indexOf("<supplies>") + 10,
                                newCharacter.indexOf("</supplies>")
                            )
                        val supplies = suppliesStr.split("\\n").toMutableList()

                        supplies.removeAll { element -> element.contains("wpn_") || element.contains("ammo_") }

                        val rifle = rifles.random().split(' ')
                        val pistol = pistols.random().split(' ')
                        val newWeapons =
                            "\n${rifle[0]} = 1 \\n\n${rifle[Random.nextInt(rifle.size - 1) + 1]} = 1 \\n\n" +
                                    "${pistol[0]} = 1 \\n\n${pistol[Random.nextInt(pistol.size - 1) + 1]} = 1 "
                        supplies.add(1, newWeapons)
                        var newSupplies = ""
                        for (i in supplies.dropLast(1)) {
                            newSupplies += "$i\\n"
                        }
                        newSupplies += supplies.last()

                        newCharacter = newCharacter.replace(suppliesStr, newSupplies)
                    }
                }

                newCharDesc += "$newCharacter</specific_character>"
            }

            newCharDesc += "</xml>"

            val exportFile = File(exportDir, file.name)
            if (exportFile.createNewFile()) {
                exportFile.writeText(newCharDesc)
            } else {
                errorMap[exportFile.path] = "Ошибка создания файла"

            }
        } else {
            errorMap[file.path] = "Ошибка чтения файла НПС"
        }
    }

    return errorMap
}

fun generateWeapons(gamedata: File): MutableMap<String, String> {
    val errorMap = mutableMapOf<String, String>()
    val weaponsDir = File(pathToWeapons)
    if (!weaponsDir.isDirectory) {
        return mutableMapOf(Pair(weaponsDir.path, "Ошибка чтения директории"))
    }

    val exportDir = File(gamedata, "configs/weapons")
    if (!exportDir.mkdirs()) {
        return mutableMapOf(Pair(exportDir.path, "Ошибка создания директории"))
    }

    for (weaponFile in weaponsDir.listFiles()!!) {
        if (!weaponFile.canRead()) {
            errorMap[weaponFile.path] = "Ошибка чтения файла"
            continue
        }

        if (!weaponFile.isFile) {
            errorMap[weaponFile.path] = "Это директория, требуется файл"
            continue
        }

        var weapon = weaponFile.readText()
        val magSize = Random.nextInt(1, 26) * multiplier(30, 2)

        weapon = replaceStat(weapon, "cost", Random.nextInt(100, 3000) * multiplier(15, 10))
        weapon = replaceStat(weapon, "ammo_limit", magSize * 3)
        weapon = replaceStat(weapon, "ammo_elapsed", magSize)
        weapon = replaceStat(weapon, "ammo_mag_size", magSize)
        weapon = replaceStat(
            weapon, "inv_weight",
            myRound(Random.nextDouble() * 5 * multiplier(30, 2) + 0.2, 2)
        )
        weapon = replaceStat(
            weapon, "fire_dispersion_base",
            myRound(Random.nextDouble() * 0.75 * multiplier(50, 2) + 0.01, 2)
        )
        weapon = replaceStat(
            weapon, "misfire_probability",
            myRound(Random.nextDouble() * 0.01 + 0.001, 3)
        )
        weapon = replaceStat(
            weapon, "condition_shot_dec",
            myRound(Random.nextDouble() * 0.001 + 0.0001, 4)
        )
        weapon = replaceStat(
            weapon, "hit_power",
            myRound(Random.nextDouble() * multiplier(5, 30) + 0.01, 2)
        )
        weapon = replaceStat(weapon, "hit_impulse", Random.nextInt(25, 501))
        weapon = replaceStat(weapon, "fire_distance", Random.nextInt(1, 1001))
        weapon = replaceStat(weapon, "bullet_speed", Random.nextInt(10, 1001) * multiplier(20, 3))
        weapon = replaceStat(weapon, "rpm", Random.nextInt(1, 1001))
        weapon = replaceStat(
            weapon, "silencer_hit_power",
            myRound(Random.nextDouble() * multiplier(5, 30) + 0.01, 2)
        )
        weapon = replaceStat(weapon, "silencer_hit_impulse", Random.nextInt(25, 501))
        weapon = replaceStat(weapon, "silencer_fire_distance", Random.nextInt(1, 1001))
        weapon = replaceStat(weapon, "silencer_bullet_speed", Random.nextInt(10, 1001) * multiplier(20, 3))

        val exportFile = File(exportDir, weaponFile.name)
        if (exportFile.createNewFile()) {
            exportFile.writeText(weapon)
        } else {
            errorMap[exportFile.path] = "Ошибка создания файла"
        }
    }

    return errorMap
}

fun generateArtefacts(gamedata: File): MutableMap<String, String> {
    val miscDir = File(pathToMisc)

    if (!miscDir.isDirectory) {
        return mutableMapOf(Pair(miscDir.path, "Ошибка чтения директории"))
    }
    val exportDir = File(gamedata, "configs/misc")
    if (!exportDir.mkdirs()) {
        return mutableMapOf(Pair(exportDir.path, "Ошибка создания директории"))
    }

    val artefactsFile = File(miscDir, "artefacts.ltx")
    if (!artefactsFile.canRead()) {
        return mutableMapOf(Pair(artefactsFile.path, "Ошибка чтения файла"))
    }
    if (!artefactsFile.isFile) {
        return mutableMapOf(Pair(artefactsFile.path, "Это директория, требуется файл"))
    }

    val fullAfStats: List<String> = listOf(
        "burn_immunity", "shock_immunity", "telepatic_immunity", "chemical_burn_immunity",
        "radiation_restore_speed", "health_restore_speed",
        "power_restore_speed", "bleeding_restore_speed",
        "additional_inventory_weight"
    )

    val originalArtefactsList = artefactsFile.readText().split(":af_base")
    var outputArtefactsText = originalArtefactsList[0]
    val newArtefactsList = originalArtefactsList.drop(1).toMutableList()

    for (i in 0 until newArtefactsList.size) {
        val statsNum = Random.nextInt(1, 5)
        //сортировка нужна!
        val newAfStats = generateAfStats(statsNum, fullAfStats).sortedBy { pair -> pair.first }

        newArtefactsList[i] = replaceStat(
            newArtefactsList[i], "cost",
            Random.nextInt(3000, 8000) * multiplier(30, 2)
        )
        newArtefactsList[i] = replaceStat(
            newArtefactsList[i], "inv_weight",
            myRound(Random.nextDouble() + 0.3, 2)
        )

        for (stat in fullAfStats) {
            newArtefactsList[i] = replaceStat(newArtefactsList[i], stat, .0)
        }
        for (stat in newAfStats) {
            newArtefactsList[i] = replaceStat(newArtefactsList[i], stat.first, stat.second)
        }
        outputArtefactsText += ":af_base" + newArtefactsList[i]
    }

    val exportFile = File(exportDir, "artefacts.ltx")
    if (exportFile.createNewFile()) {
        exportFile.writeText(outputArtefactsText)
    } else {
        return mutableMapOf(Pair(exportFile.path, "Ошибка записи файла"))
    }

    return mutableMapOf()
}

fun generateOutfits(gamedata: File): MutableMap<String, String> {
    val miscDir = File(pathToMisc)

    if (!miscDir.isDirectory) {
        return mutableMapOf(Pair(miscDir.path, "Ошибка чтения директории"))
    }
    val exportDir = File(gamedata, "configs/misc")
    if (!exportDir.exists()) {
        if (!exportDir.mkdirs()) {
            return mutableMapOf(Pair(exportDir.path, "Ошибка создания директории"))
        }
    }

    val outfitsFile = File(miscDir, "outfit.ltx")
    if (!outfitsFile.canRead()) {
        return mutableMapOf(Pair(outfitsFile.path, "Ошибка чтения файла"))
    }
    if (!outfitsFile.isFile) {
        return mutableMapOf(Pair(outfitsFile.path, "Это директория, требуется файл"))
    }

    val firstOutfitStats: List<String> = listOf(
        "burn_protection", "shock_protection", "radiation_protection", "chemical_burn_protection",
        "telepatic_protection"
    )
    val secondOutfitStats: List<String> = listOf(
        "strike_protection", "explosion_protection", "wound_protection",
        "fire_wound_protection", "physic_strike_wound_immunity"
    )

    val originalArtefactsList = outfitsFile.readText().split(":outfit_base")
    var outputOutfitsText = originalArtefactsList[0] + ":outfit_base" + originalArtefactsList[1]
    val newOutfitsList = originalArtefactsList.drop(2).toMutableList()

    for (i in 0 until newOutfitsList.size) {
        val maxWeight = Random.nextInt(-10, 31)
        val afCount = Random.nextInt(1, 6) / multiplier(50, 2)

        newOutfitsList[i] = replaceStat(
            newOutfitsList[i], "inv_weight",
            Random.nextInt(1, 11) * multiplier(20, 2)
        )
        newOutfitsList[i] = replaceStat(
            newOutfitsList[i], "cost",
            Random.nextInt(1000, 7000) * multiplier(15, 8)
        )

        for (stat in firstOutfitStats) {
            newOutfitsList[i] = replaceStat(
                newOutfitsList[i], stat,
                myRound(Random.nextDouble() * 0.1 / multiplier(80, 2), 2)
            )
        }
        for (stat in secondOutfitStats) {
            newOutfitsList[i] = replaceStat(
                newOutfitsList[i], stat,
                myRound(Random.nextDouble() * 0.6 / multiplier(80, 2), 2)
            )
        }

        if (newOutfitsList[i].contains("additional_inventory_weight")) {
            newOutfitsList[i] = replaceStat(newOutfitsList[i], "additional_inventory_weight", maxWeight)
        } else {
            newOutfitsList[i] = newOutfitsList[i].replaceFirst(
                "immunities_sect",
                "additional_inventory_weight = $maxWeight\nimmunities_sect"
            )
        }
        if (newOutfitsList[i].contains("additional_inventory_weight2")) {
            newOutfitsList[i] = replaceStat(newOutfitsList[i], "additional_inventory_weight2", maxWeight + 10)
        } else {
            newOutfitsList[i] = newOutfitsList[i].replaceFirst(
                "immunities_sect",
                "additional_inventory_weight2 = ${maxWeight + 10}\nimmunities_sect"
            )
        }
        if (newOutfitsList[i].contains("artefact_count")) {
            newOutfitsList[i] = replaceStat(newOutfitsList[i], "artefact_count", afCount)
        } else {
            newOutfitsList[i] = newOutfitsList[i].replaceFirst(
                "immunities_sect",
                "artefact_count = $afCount\nimmunities_sect"
            )
        }

        outputOutfitsText += ":outfit_base" + newOutfitsList[i]
    }

    val exportFile = File(exportDir, "outfit.ltx")
    if (exportFile.createNewFile()) {
        exportFile.writeText(outputOutfitsText)
    } else {
        return mutableMapOf(Pair(exportFile.path, "Ошибка записи файла"))
    }

    return mutableMapOf()
}

fun generateStashes(
    gamedata: File,
    rifles: List<String>,
    pistols: List<String>,
    items: List<String>,
    outfits: List<String>,
    devices: List<String>,
    artefacts: List<String>,
    ammos: List<String>
): MutableMap<String, String> {
    val errorMap: MutableMap<String, String> = mutableMapOf()
    val miscDir = File(pathToMisc)

    if (!miscDir.isDirectory) {
        return mutableMapOf(Pair(miscDir.path, "Ошибка чтения директории"))
    }
    val exportDir = File(gamedata, "configs/misc")
    if (!exportDir.exists()) {
        if (!exportDir.mkdirs()) {
            return mutableMapOf(Pair(exportDir.path, "Ошибка создания директории"))
        }
    }

    val stashFiles = miscDir.listFiles()!!.toMutableList()
    stashFiles.removeIf { file -> !file.name.contains("treasure_") || !file.isFile }
    if (stashFiles.isEmpty()) {
        return mutableMapOf(Pair("miscDir.listFiles", "Не найдены файлы с тайниками"))
    }

    for (stashFile in stashFiles) {
        if (!stashFile.canRead()) {
            errorMap[stashFile.path] = "Ошибка чтения файла"
            continue
        }

        val originalStashList = stashFile.readText().split("items")
        var outputStashText = originalStashList[0]
        val newStashList = originalStashList.drop(1)

        for (i in newStashList) {
            var newItems = ""
            val itemCount = Random.nextInt(1, 6)

            for (j in 0 until itemCount) {
                val whichItemType: Int = Random.nextInt(100)

                newItems += when {
                    whichItemType < 10 -> {
                        generateItem(outfits, 1)
                    }

                    whichItemType < 20 -> {
                        generateItem(rifles, 1)
                    }

                    whichItemType < 30 -> {
                        generateItem(artefacts, 2)
                    }

                    whichItemType < 45 -> {
                        generateItem(pistols, 1)
                    }

                    whichItemType < 70 -> {
                        generateItem(ammos, 6)
                    }

                    whichItemType < 95 -> {
                        generateItem(items, 8)
                    }

                    else -> {
                        generateItem(devices, 1)
                    }

                }

                if (j < itemCount - 1) {
                    newItems += ", ";
                }
            }

            val oldItems = i.substring(0, i.indexOf('\n'))
            outputStashText += "items = " + i.replace(oldItems, newItems)
        }

        val exportFile = File(exportDir, stashFile.name)
        if (exportFile.createNewFile()) {
            exportFile.writeText(outputStashText)
        } else {
            errorMap[exportFile.path] = "Ошибка создания файла"
        }
    }

    return errorMap
}

fun copyTranslate(gamedata: File): MutableMap<String, String> {
    val textFile = File(pathToText)
    if (!textFile.exists() || textFile.isFile || !textFile.canRead()) {
        return mutableMapOf(Pair(textFile.path, "Ошибка чтения директории с косячным переводом"))
    }

    val newConfigsFile = File(gamedata, "configs/text")
    if (!newConfigsFile.exists() && !newConfigsFile.mkdirs()) {
        return mutableMapOf(Pair(newConfigsFile.path, "Ошибка создания директории"))
    }

    if(!textFile.copyRecursively(newConfigsFile)){
        return mutableMapOf(Pair(newConfigsFile.path, "Ошибка копирования директории"))
    }

    return mutableMapOf()
}

fun replaceStat(item: String, statName: String, statValue: Int): String {
    if (item.contains(statName)) {
        val tmp = item.substring(item.indexOf(statName))
        return item.replace(tmp.substring(0, tmp.indexOf('\n')), "$statName = $statValue")
    }
    return item
}

fun replaceStat(item: String, statName: String, statValue: Double): String {
    if (item.contains(statName)) {
        val tmp = item.substring(item.indexOf(statName))
        return item.replace(tmp.substring(0, tmp.indexOf('\n')), "$statName = $statValue")
    }
    return item
}

fun multiplier(probability: Int, multValue: Int): Int {
    if (Random.nextInt(100) < probability) {
        return multValue
    }
    return 1
}

fun myRound(value: Double, numCount: Int): Double {
    return when (numCount) {
        1 -> (value * 10).roundToLong() / 10.0
        2 -> (value * 100).roundToLong() / 100.0
        3 -> (value * 1000).roundToLong() / 1000.0
        4 -> (value * 10000).roundToLong() / 10000.0
        else -> value
    }
}

fun printLogs(errorMap: MutableMap<String, String>, exception: Exception? = null) {
    for (el in errorMap) {
        println("${el.key} : ${el.value}")
    }
    if (exception != null) {
        println(exception.message)
        println(exception.cause)
        for (trace in exception.stackTrace) {
            println(trace)
        }
    }

    println("Для продолжения нажмите Enter...")
    readlnOrNull()
}

fun generateAfStats(statsNum: Int, fullAfStats: List<String>): List<Pair<String, Double>> {
    val statsList = fullAfStats.drop(0).toMutableList()
    val tmpList: MutableList<Pair<String, Double>> = mutableListOf()

    for (i in 0 until statsNum) {
        var rndStat: Double
        val rndInd = Random.nextInt(0, statsList.size)
        val currStat = statsList[rndInd]
        statsList.removeAt(rndInd)
        when (currStat) {
            "radiation_restore_speed" -> {
                rndStat = myRound((Random.nextDouble() * 0.014 - 0.008) / multiplier(70, 2), 3)
            }

            "health_restore_speed" -> {
                rndStat = myRound((Random.nextDouble() * 0.012 - 0.005) / multiplier(70, 2), 3)
            }

            "power_restore_speed" -> {
                rndStat = myRound((Random.nextDouble() * 0.012 - 0.005) / multiplier(70, 2), 3)
            }

            "bleeding_restore_speed" -> {
                rndStat = myRound((Random.nextDouble() * 0.12 - 0.05) / multiplier(70, 2), 2)
            }

            "additional_inventory_weight" -> {
                rndStat = myRound(Random.nextInt(-15, 31).toDouble(), 2)
            }

            else -> {
                rndStat = myRound((Random.nextDouble() * 0.012 - 0.005) / multiplier(70, 2), 3)
            }
        }
        tmpList.add(Pair(currStat, rndStat))
    }
    return tmpList
}

fun generateItem(itemList: List<String>, maxItemCount: Int): String {
    var itemPackCount = 1
    var item = itemList.random()

    if (item.contains(" ")) {
        try {
            itemPackCount = item.split(' ')[1].toInt()
        } catch (_: Exception) {
        }
        item = item.split(' ')[0]
    }

    val count = Random.nextInt(1, maxItemCount + 1) * itemPackCount

    return "$item, $count";
}

fun getUserInput(oldAnswer: String, message: String): String {
    var answer: String? = null
    if (oldAnswer != "2") {
        println(message)
        do {
            answer = readlnOrNull()
        } while (answer != "1" && answer != "0")
    }
    return answer ?: oldAnswer
}