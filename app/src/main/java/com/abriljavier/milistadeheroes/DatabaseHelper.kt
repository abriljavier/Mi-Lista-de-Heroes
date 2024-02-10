package com.abriljavier.milistadeheroes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.abriljavier.milistadeheroes.dataclasses.Attributes
import com.abriljavier.milistadeheroes.dataclasses.Background
import com.abriljavier.milistadeheroes.dataclasses.Classe
import com.abriljavier.milistadeheroes.dataclasses.Feature
import com.abriljavier.milistadeheroes.dataclasses.Features
import com.abriljavier.milistadeheroes.dataclasses.Level
import com.abriljavier.milistadeheroes.dataclasses.Personaje
import com.abriljavier.milistadeheroes.dataclasses.Race
import com.abriljavier.milistadeheroes.dataclasses.Traits
import com.abriljavier.milistadeheroes.dataclasses.Users
import java.sql.Types.NULL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "milistadeheroes.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable =
            "CREATE TABLE users (" + "user_id INTEGER PRIMARY KEY AUTOINCREMENT," + "username TEXT NOT NULL," + "password TEXT NOT NULL," + "pc_id INTEGER DEFAULT NULL," + "FOREIGN KEY(pc_id) REFERENCES pcs(id) ON UPDATE CASCADE)"
        db.execSQL(createUserTable)

        val createRacesTable =
            "CREATE TABLE races (" + "race_id INTEGER PRIMARY KEY AUTOINCREMENT," + "race_name TEXT NOT NULL," + "characteristic_boost TEXT NOT NULL," + "size TEXT NOT NULL," + "speed INTEGER NOT NULL," + "languages TEXT NOT NULL," + "attributes TEXT DEFAULT NULL)"
        db.execSQL(createRacesTable)

        val createBackgroundsTable =
            "CREATE TABLE backgrounds (" + "background_id INTEGER PRIMARY KEY AUTOINCREMENT," + "bg_name TEXT DEFAULT NULL," + "competencies TEXT DEFAULT NULL," + "tools TEXT DEFAULT NULL," + "languages TEXT DEFAULT NULL," + "items TEXT DEFAULT NULL," + "rasgos TEXT DEFAULT NULL)"
        db.execSQL(createBackgroundsTable)

        val createClassesTable =
            "CREATE TABLE classes (" + "class_id INTEGER PRIMARY KEY AUTOINCREMENT," + "class_name TEXT NOT NULL," + "hit_die TEXT NOT NULL," + "saving_throw_proficiencies TEXT NOT NULL," + "habilities_proficiencies TEXT NOT NULL," + "armor_weapon_proficiencies TEXT NOT NULL)"
        db.execSQL(createClassesTable)

        val createClassLevelsTable =
            "CREATE TABLE class_levels (" + "level_id INTEGER PRIMARY KEY AUTOINCREMENT," + "class_id INTEGER NOT NULL," + "level INTEGER NOT NULL," + "feature_name TEXT NOT NULL," + "description TEXT NOT NULL," + "FOREIGN KEY(class_id) REFERENCES classes(class_id) ON DELETE CASCADE ON UPDATE CASCADE)"
        db.execSQL(createClassLevelsTable)

        val createPersonajesTable =
            "CREATE TABLE personajes (" + "personaje_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "user_id INTEGER NOT NULL, " + "name TEXT NOT NULL, " + "imageUri TEXT, " + "class TEXT, " + "race TEXT, " + "level INTEGER DEFAULT 1, " + "hitPoints INTEGER, "+"competencies TEXT," + "attributes TEXT, " + "alignment TEXT, " + "appearance TEXT, " + "history TEXT, " + "languages TEXT, " + "notes TEXT, " + "FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE" + ")"
        db.execSQL(createPersonajesTable)


        insertRaces(db)
        insertBackgrounds(db)
        insertClasses(db)
        insertLevel(db)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("No hace falta implementar")
    }

    // USUARIOS
    fun insertUser(user: Users) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("password", user.password)
            put("pc_id", user.pc_id)
        }
        db.insert("users", null, values)
        db.close()
    }

    fun getUserByUsername(username: String): Users? {
        val db = this.readableDatabase
        var user: Users? = null
        val selection = "username = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(
            "users", null, selection, selectionArgs, null, null, null
        )
        if (cursor.moveToFirst()) {
            val userId = cursor.getInt(0)
            val userName = cursor.getString(1)
            val password = cursor.getString(2)
            val pcId = cursor.getInt(3)
            user = Users(userId, userName, password, pcId)
        }
        cursor.close()
        db.close()
        return user
    }

    fun updateUserPassword(userId: Int, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("password", newPassword)
        val whereClause = "user_id = ?"
        val whereArgs = arrayOf(userId.toString())
        val rowsAffected = db.update("users", values, whereClause, whereArgs)
        return rowsAffected > 0
    }

    // RAZAS
    fun getAllRaces(): List<Race> {

        val gson = Gson()

        val races = mutableListOf<Race>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM races"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val attributesJson = cursor.getString(2)
                val attributes = gson.fromJson(attributesJson, Attributes::class.java)
                val size = cursor.getString(3)
                val speed = cursor.getInt(4)
                val languages = cursor.getString(5)
                val featuresJson = cursor.getString(6)
                val features = gson.fromJson(featuresJson, Features::class.java)
                races.add(Race(id, name, attributes, size, speed, languages, features))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return races
    }

    //CLASES
    fun getAllClasses(): List<Classe> {
        val classes = mutableListOf<Classe>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM classes"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val classId = cursor.getInt(0)
                val className = cursor.getString(1)
                val hitDie = cursor.getString(2)
                val savingThrowProficiencies = cursor.getString(3)
                val abilitiesProficiencies = cursor.getString(4)
                val armorWeaponProficiencies = cursor.getString(5)

                classes.add(
                    Classe(
                        classId = classId,
                        className = className,
                        hitDie = hitDie,
                        savingThrowProficiencies = savingThrowProficiencies,
                        abilitiesProficiencies = abilitiesProficiencies,
                        armorWeaponProficiencies = armorWeaponProficiencies
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return classes
    }

    //TRASFONDOS
    fun getAllBackgrounds(): List<Background> {
        val db = this.readableDatabase
        val backgrounds = mutableListOf<Background>()
        val gson = Gson()
        val cursor = db.query("backgrounds", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val traitsJson = cursor.getString(6)
            val traits = gson.fromJson(traitsJson, Traits::class.java)

            val background = Background(
                backgroundId = cursor.getInt(0),
                bgName = cursor.getString(1),
                competencies = cursor.getString(2),
                tools = cursor.getString(3),
                languages = cursor.getInt(4),
                items = cursor.getString(5),
                traits = traits
            )

            backgrounds.add(background)
        }
        cursor.close()
        db.close()
        return backgrounds
    }

    // NIVELES
    fun getFeaturesByClassIdAndLevel(classId: Int, level: Int): List<Feature> {
        val db = this.readableDatabase
        val featuresList = mutableListOf<Feature>()
        val selectQuery =
            "SELECT * FROM class_levels WHERE class_id = ? AND level <= ? ORDER BY level ASC"

        db.rawQuery(selectQuery, arrayOf(classId.toString(), level.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                val featureName = cursor.getString(cursor.getColumnIndexOrThrow("feature_name"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                featuresList.add(Feature(featureName, description))
            }
        }

        return featuresList
    }

    //PERSONAJES
    fun addPersonaje(personaje: Personaje, userId: Int) {
        val gson = Gson()
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("name", personaje.name)
            put("imageUri", personaje.imageUri)
            put("class", personaje.characterClass?.className)
            put("race", personaje.race?.name)
            put("level", personaje.numLevel)
            put("hitPoints", personaje.hitPoints)
            val attributesJson = gson.toJson(personaje.attributes)
            put("attributes", attributesJson)
            val competenciesJson = gson.toJson(personaje.competiences)
            put("competencies", competenciesJson)
            put ("alignment", personaje.selectedAligment)
            put("history", personaje.history)
            put("languages", personaje.languages)
            put("notes", personaje.notes)
        }

        val personajeId = db.insert("personajes", null, values)
        db.close()
    }

    fun getPersonajesByUserId(userId: Int): List<Personaje> {
        val list = mutableListOf<Personaje>()
        val db = this.readableDatabase
        val cursor = db.query(
            "personajes",
            null,
            "user_id=?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(2)
                val imageUri = cursor.getString(3)
                val className = cursor.getString(4)
                val race = cursor.getString(5)
                val level = cursor.getInt(6)
                val hitPoints = cursor.getInt(7)
                val competencies = cursor.getString(8)
                val attributesJson = cursor.getString(9)
                val alignment = cursor.getString(10)
                val appearance = cursor.getString(11)
                val history = cursor.getString(12)
                val languages = cursor.getString(13)
                val notes = cursor.getString(14)

                val gson = Gson()
                val attributes = gson.fromJson(attributesJson, Attributes::class.java)

                list.add(Personaje(
                    name = name,
                    imageUri = imageUri,
                    characterClass = Classe(className = className),
                    race = Race(name = race),
                    numLevel = level,
                    hitPoints = hitPoints,
                    competiences = mutableListOf(competencies),
                    attributes = attributes,
                    selectedAligment = alignment,
                    appearance = appearance,
                    history = history,
                    languages = languages,
                    notes = notes
                ))
                println(list)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}

private fun insertRaces(db: SQLiteDatabase) {

    val gson = Gson()

    val races = arrayOf(
        Race(
            id = null, name = "Draconido", attributes = Attributes(
                STR = 2, DEX = 0, CON = 0, INT = 0, WIS = 0, CHA = 1
            ), size = "Mediano", speed = 30, languages = "Común y Dracónico", features = Features(
                first = "Linaje Dracónico: Posees la sangre de dragones. Escoge de qué tipo en la tabla linaje dracónico. Tu Ataque de Aliento y Resistencia al Daño vendrán de terminadas por el tipo de dragón, tal y como se indica en dicha tabla.",
                second = "Ataque de Aliento Puedes utilizar tu acción para exhalar energía destructora. Tu Linaje Dracónico determina el tamaño, forma y tipo de daño del aliento. Cuando uses tu Ataque de Aliento, las criaturas que se encuentren en la zona cubierta por este deberán hacer una tirada de salvación, cuyo tipo viene definido por tu linaje. La CD de esta tirada de salvación es de 8 + tu modificador por Constitución + tu bonificador por competencia. Las criaturas sufrirán 2d6 de daño si fallan la tirada o la mitad de ese daño si la superan. El daño aumenta a 3d6 cuando alcanzas el nivel 6, a 4d6 a nivel 11 y a 5d6 a nivel 16. Una vez utilizado el Ataque de Aliento, no podrás volver a emplearlo hasta que finalices un descanso corto o largo.",
                third = null,
                fourth = "Resistencia al Daño. Posees resistencia al tipo de daño asociado a tu Linaje Dracónico.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = null, name = "Enano de las colinas", attributes = Attributes(
                STR = 0, DEX = 0, CON = 2, INT = 0, WIS = 1, CHA = 0
            ), size = "Mediano", speed = 25, languages = "Común y Enano", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la vida bajo tierra, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Resistencia Enana. Tienes ventaja en las tiradas de salvación contra veneno y posees resistencia al daño de veneno (como se explica en el capítulo 9: 'Combate').",
                third = "Entrenamiento de Combate Enano. Eres competente con hachas de guerra, hachas de mano, martillos de guerra y martillos ligeros.",
                fourth = "Competencia con Herramientas. Eres competente con las herramientas de artesano que elijas de entre las siguientes: herramientas de albañil, herramientas de herrero o suministros de cervecero.",
                fifth = "Afinidad con la Piedra. Cuando hagas una prueba de Inteligencia (Historia) que tenga relación con el origen de un trabajo en piedra, se te considerará competente en la habilidad Historia y añadirás dos veces tu bonificador por competencia a la tirada, en lugar de solo una.",
                sixth = null
            )
        ), Race(
            id = 4, name = "Enano de las montañas", attributes = Attributes(
                STR = 2, DEX = 0, CON = 2, INT = 0, WIS = 0, CHA = 0
            ), size = "Mediano", speed = 25, languages = "Común y Enano", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la vida bajo tierra, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Resistencia Enana. Tienes ventaja en las tiradas de salvación contra veneno y posees resistencia al daño de veneno (como se explica en el capítulo 9: 'Combate').",
                third = "Entrenamiento de Combate Enano. Eres competente con hachas de guerra, hachas de mano, martillos de guerra y martillos ligeros.",
                fourth = "Competencia con Herramientas. Eres competente con las herramientas de artesano que elijas de entre las siguientes: herramientas de albañil, herramientas de herrero o suministros de cervecero.",
                fifth = "Afinidad con la Piedra. Cuando hagas una prueba de Inteligencia (Historia) que tenga relación con el origen de un trabajo en piedra, se te considerará competente en la habilidad Historia y añadirás dos veces tu bonificador por competencia a la tirada, en lugar de solo una.",
                sixth = null
            )
        ), Race(
            id = 5, name = "Elfo Alto", attributes = Attributes(
                STR = 0, DEX = 2, CON = 0, INT = 1, WIS = 0, CHA = 0
            ), size = "Mediano", speed = 30, languages = "Común y Elfico", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la penumbra de los bosques y el cielo nocturno, puedes ver bien en la oscuridad o con poca luz. Hasta a un máximo de 60 pies, eres capaz de ver con luz tenue como si hubiera luz brillante y en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Sentidos Agudos. Eres competente en la habilidad Percepción.",
                third = "Linaje Feérico. Tienes ventaja en las tiradas de salvación para evitar ser hechizado y la magia no puede dormirte.",
                fourth = "Trance. Los elfos no necesitan dormir. Meditan profundamente, en un estado semiconsciente, durante 4 horas al día. La palabra en común para referirse a esta meditación es 'trance'. Mientras meditas, experimentas algo parecido a sueños, que en realidad son ejercicios mentales que se han vuelto automáticos tras años de práctica. Este trance es suficiente para obtener los mismos beneficios que un humano recibe de 8 horas de sueño.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 6, name = "Elfo de los Bosques", attributes = Attributes(
                STR = 0, DEX = 2, CON = 0, INT = 0, WIS = 1, CHA = 0
            ), size = "M", speed = 30, languages = "Común y Elfico", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la penumbra de los bosques y el cielo nocturno, puedes ver bien en la oscuridad o con poca luz. Hasta a un máximo de 60 pies, eres capaz de ver con luz tenue como si hubiera luz brillante y en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Sentidos Agudos. Eres competente en la habilidad Percepción.",
                third = "Linaje Feérico. Tienes ventaja en las tiradas de salvación para evitar ser hechizado y la magia no puede dormirte.",
                fourth = "Trance. Los elfos no necesitan dormir. Meditan profundamente, en un estado semiconsciente, durante 4 horas al día. La palabra en común para referirse a esta meditación es 'trance'. Mientras meditas, experimentas algo parecido a sueños, que en realidad son ejercicios mentales que se han vuelto automáticos tras años de práctica. Este trance es suficiente para obtener los mismos beneficios que un humano recibe de 8 horas de sueño.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 7, name = "Elfo Drow", attributes = Attributes(
                STR = 0, DEX = 2, CON = 0, INT = 0, WIS = 0, CHA = 1
            ), size = "Mediano", speed = 30, languages = "Común y Elfico", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la penumbra de los bosques y el cielo nocturno, puedes ver bien en la oscuridad o con poca luz. Hasta a un máximo de 60 pies, eres capaz de ver con luz tenue como si hubiera luz brillante y en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Sentidos Agudos. Eres competente en la habilidad Percepción.",
                third = "Linaje Feérico. Tienes ventaja en las tiradas de salvación para evitar ser hechizado y la magia no puede dormirte.",
                fourth = "Trance. Los elfos no necesitan dormir. Meditan profundamente, en un estado semiconsciente, durante 4 horas al día. La palabra en común para referirse a esta meditación es 'trance'. Mientras meditas, experimentas algo parecido a sueños, que en realidad son ejercicios mentales que se han vuelto automáticos tras años de práctica. Este trance es suficiente para obtener los mismos beneficios que un humano recibe de 8 horas de sueño.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 8, name = "Mediano Piesligeros", attributes = Attributes(
                STR = 0, DEX = 2, CON = 0, INT = 0, WIS = 0, CHA = 1
            ), size = "Pequeño", speed = 25, languages = "Común y mediano", features = Features(
                first = "Afortunado. Cuando saques un 1 en el dado al hacer una tirada de ataque, prueba de característica o tirada de salvación, puedes volver a tirar el dado, pero tendrás que utilizar el resultado nuevo.",
                second = "Valiente. Posees ventaja en las tiradas de salvación para evitar ser asustado.",
                third = "Agilidad de Mediano. Puedes moverte a través del espacio ocupado por una criatura cuyo tamaño sea, al menos, una categoría superior al tuyo.",
                fourth = "Sigiloso por Naturaleza. Puedes intentar esconderte incluso tras una criatura cuyo tamaño sea, al menos, una categoría superior al tuyo.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 9, name = "Mediano Fornido", attributes = Attributes(
                STR = 0, DEX = 2, CON = 1, INT = 0, WIS = 0, CHA = 0
            ), size = "Pequeño", speed = 25, languages = "Común y mediano", features = Features(
                first = "Afortunado. Cuando saques un 1 en el dado al hacer una tirada de ataque, prueba de característica o tirada de salvación, puedes volver a tirar el dado, pero tendrás que utilizar el resultado nuevo.",
                second = "Valiente. Posees ventaja en las tiradas de salvación para evitar ser asustado.",
                third = "Agilidad de Mediano. Puedes moverte a través del espacio ocupado por una criatura cuyo tamaño sea, al menos, una categoría superior al tuyo.",
                fourth = "Sigiloso por Naturaleza. Puedes intentar esconderte incluso tras una criatura cuyo tamaño sea, al menos, una categoría superior al tuyo.",
                fifth = "Resistencia de Fornido. Tienes ventaja en las tiradas de salvación contra veneno y posees resistencia al daño de veneno.",
                sixth = null
            )
        ), Race(
            id = 18, name = "Humano", attributes = Attributes(
                STR = 1, DEX = 1, CON = 1, INT = 1, WIS = 1, CHA = 1
            ), size = "Mediano", speed = 30, languages = "Común", features = Features(
                first = null, second = null, third = null, fourth = null, fifth = null, sixth = null
            )
        ), Race(
            id = 19, name = "Gnomo de los bosques", attributes = Attributes(
                STR = 0, DEX = 1, CON = 0, INT = 2, WIS = 0, CHA = 0
            ), size = "Pequeño", speed = 25, languages = "Común y gnomo", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la vida bajo tierra, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Astucia Gnoma. Tienes ventaja en las tiradas de salvación de Inteligencia, Sabiduría y Carisma contra magia.",
                third = "Ilusionista Innato. Conoces el truco ilusión menor. La Inteligencia es tu aptitud mágica para lanzarlo.",
                fourth = "Hablar con las Bestezuelas. Puedes comunicar ideas sencillas a bestias de tamaño Pequeño o inferior usando gestos y sonidos. Los gnomos de los bosques adoran a los animales y en ocasiones adoptan ardillas, tejones, conejos, topos, pájaros carpinteros y otras criaturas similares como mascotas.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 20, name = "Gnomo de las rocas", attributes = Attributes(
                STR = 0, DEX = 0, CON = 1, INT = 2, WIS = 0, CHA = 0
            ), size = "Pequeño", speed = 25, languages = "Común y gnomo", features = Features(
                first = "Visión en la Oscuridad. Acostumbrado a la vida bajo tierra, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Astucia Gnoma. Tienes ventaja en las tiradas de salvación de Inteligencia, Sabiduría y Carisma contra magia.",
                third = "Ilusionista Innato. Conoces el truco ilusión menor. La Inteligencia es tu aptitud mágica para lanzarlo.",
                fourth = "Saber del Artificiero. Cuando hagas una prueba de Inteligencia (Historia) que tenga relación con objetos mágicos, alquímicos o tecnológicos, se te considerará competente en la habilidad Historia y añadirás dos veces tu bonificador por competencia a la tirada, en lugar de solo una.",
                fifth = "Manitas. Eres competente con las siguientes herramientas de artesano: herramientas de manitas. Usándolas, puedes invertir 1 hora y materiales valorados en 10 po para construir un dispositivo de relojería de tamaño Diminuto (CA 5, 1 punto de golpe). Este dispositivo deja de funcionar pasadas 24 horas (salvo si dedicas 1 hora a repararlo para mantenerlo a punto) o si usas una acción para desmantelarlo. De hacer esto último, puedes recuperar los materiales que usaste en la construcción. Puedes tener hasta tres dispositivos de este tipo funcionando al mismo tiempo.",
                sixth = null
            )
        ), Race(
            id = 21, name = "Semielfo", attributes = Attributes(
                STR = 0, DEX = 0, CON = 0, INT = 0, WIS = 0, CHA = 2
            ), size = "Mediano", speed = 30, languages = "Común y elfo", features = Features(
                first = "Visión en la Oscuridad. Debido a tu sangre élfica, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Linaje Feérico. Tienes ventaja en las tiradas de salvación para evitar ser hechizado y la magia no puede dormirte.",
                third = "Versátil con Habilidades. Ganas competencia en dos habilidades de tu elección.",
                fourth = null,
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 22, name = "Semiorco", attributes = Attributes(
                STR = 2, DEX = 0, CON = 1, INT = 0, WIS = 0, CHA = 0
            ), size = "Mediano", speed = 30, languages = "Común y orco", features = Features(
                first = "Visión en la Oscuridad. Debido a tu sangre orca, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Amenazador. Eres competente en la habilidad Intimidación.",
                third = "Aguante Incansable. Cuando tus puntos de golpe se reducen a 0 pero no mueres instantáneamente, puedes volver a tener 1 punto de golpe. Una vez utilizado este atributo, deberás terminar un descanso largo para poder volver a usarlo otra vez.",
                fourth = "Ataques Salvajes. Cuando hagas un crítico con un ataque con arma cuerpo a cuerpo, podrás tirar uno de los dados de daño del arma una vez más y añadir el resultado al daño adicional causado por el crítico.",
                fifth = null,
                sixth = null
            )
        ), Race(
            id = 23, name = "Tiefling", attributes = Attributes(
                STR = 0, DEX = 0, CON = 0, INT = 1, WIS = 0, CHA = 2
            ), size = "Mediano", speed = 30, languages = "Común e infernal", features = Features(
                first = "Visión en la Oscuridad. Debido a tu sangre infernal, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.",
                second = "Resistencia Infernal. Posees resistencia al daño de fuego.",
                third = "Linaje Infernal. Conoces el truco taumaturgia. Cuando llegas a nivel 3, puedes lanzar el conjuro reprensión infernal como conjuro de nivel 2 una vez usando este atributo y recuperas la capacidad para hacerlo tras realizar un descanso largo. Cuando alcanzas el nivel 5, eres capaz de lanzar el conjuro oscuridad una vez empleando este atributo y recuperas la capacidad para hacerlo tras realizar un descanso largo. El Carisma es tu aptitud mágica para estos conjuros.",
                fourth = null,
                fifth = null,
                sixth = null
            )
        )
    )


    races.forEach { race ->
        val attributesJson = gson.toJson(race.attributes)
        val featuresJson = gson.toJson(race.features)

        val values = ContentValues().apply {
            put("race_id", race.id)
            put("race_name", race.name)
            put("characteristic_boost", attributesJson)
            put("size", race.size)
            put("speed", race.speed)
            put("languages", race.languages)
            put("attributes", featuresJson)
        }

        db.insert("races", null, values)
    }
}

private fun insertBackgrounds(db: SQLiteDatabase) {
    val backgrounds = arrayOf(
        Background(
            backgroundId = NULL,
            bgName = "Acolito",
            competencies = "Perspicacia, religión",
            tools = "",
            languages = 2,
            items = "Símbolo sagrado (un regalo de cuando fuiste ordenado sacerdote), devocionario o rueda de oraciones, 5 varas de incienso, vestiduras, muda de ropas comunes y una bolsa con 15 po",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Idolatro a un héroe particular de mi fe y constantemente me refiero a las hazañas y el ejemplo de esa persona.",
                    2 to "Puedo encontrar puntos en común entre los enemigos más feroces, empatizando con ellos y siempre trabajando hacia la paz.",
                    3 to "Veo presagios en cada evento y acción. Los dioses intentan hablarnos, solo necesitamos escuchar.",
                    4 to "Nada puede sacudir mi actitud optimista.",
                    5 to "Cito (o malinterpreto) textos sagrados y proverbios en casi todas las situaciones.",
                    6 to "Soy tolerante (o intolerante) con otras religiones y respeto (o condeno) el culto a otros dioses.",
                    7 to "He disfrutado de comida, bebida y alta sociedad entre la élite de mi templo. La vida ruda me irrita.",
                    8 to "He pasado tanto tiempo en el templo que tengo poca experiencia práctica tratando con personas en el mundo exterior."
                ), ideals = mapOf(
                    1 to "Tradición. Las antiguas tradiciones de adoración y sacrificio deben ser preservadas y mantenidas. (Legal)",
                    2 to "Caridad. Siempre trato de ayudar a los necesitados, sin importar el costo personal. (Bueno)",
                    3 to "Cambio. Debemos ayudar a traer los cambios que los dioses constantemente están trabajando en el mundo. (Caótico)",
                    4 to "Poder. Espero algún día ascender a la cima de la jerarquía religiosa de mi fe. (Legal)",
                    5 to "Fe. Confío en que mi deidad guiará mis acciones. Tengo fe en que si trabajo duro, las cosas irán bien. (Legal)",
                    6 to "Aspiración. Busco demostrarme digno del favor de mi dios al comparar mis acciones con sus enseñanzas. (Cualquiera)"
                ), links = mapOf(
                    1 to "Moriría para recuperar un antiguo relicario de mi fe que se perdió hace mucho tiempo.",
                    2 to "Algún día me vengaré de la corrupta jerarquía del templo que me etiquetó como hereje.",
                    3 to "Le debo mi vida al sacerdote que me acogió cuando mis padres murieron.",
                    4 to "Todo lo que hago es para la gente común.",
                    5 to "Haré cualquier cosa para proteger el templo donde serví.",
                    6 to "Busco preservar un texto sagrado que mis enemigos consideran herético y buscan destruir."
                ), flaws = mapOf(
                    1 to "Juzgo a los demás severamente, y a mí mismo aún más.",
                    2 to "Confío demasiado en aquellos que tienen poder dentro de la jerarquía de mi templo.",
                    3 to "Mi piedad a veces me lleva a confiar ciegamente en aquellos que profesan fe en mi dios.",
                    4 to "Soy inflexible en mi pensamiento.",
                    5 to "Desconfío de los extraños y espero lo peor de ellos.",
                    6 to "Una vez que elijo un objetivo, me obsesiono con ello en detrimento de todo lo demás en mi vida."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Animador",
            competencies = "Acrobacias, Interpretación.",
            tools = "Útiles para disfrazarse, un tipo de instrumento musical. ",
            languages = NULL,
            items = "Instrumento musical (a tu e lección), el favor de un admirador (carta de amor, bucle de cabello o bagatela), disfraz y una bolsa con 15 po. ",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Conozco una historia relevante para casi cualquier situación.",
                    2 to "Siempre que llego a un lugar nuevo, recojo rumores locales y difundo chismes.",
                    3 to "Soy un romántico empedernido, siempre en busca de ese 'alguien especial'.",
                    4 to "Nadie permanece enfadado conmigo o cerca de mí durante mucho tiempo, ya que puedo calmar cualquier cantidad de tensión.",
                    5 to "Me encanta una buena broma, incluso si está dirigida a mí.",
                    6 to "Me vuelvo amargado si no soy el centro de atención.",
                    7 to "No me conformaré con nada menos que la perfección.",
                    8 to "Cambio mi estado de ánimo o mi opinión tan rápidamente como cambio de tono en una canción."
                ), ideals = mapOf(
                    1 to "Belleza. Cuando actúo, hago que el mundo sea mejor de lo que era. (Bueno)",
                    2 to "Tradición. Las historias, leyendas y canciones del pasado nunca deben ser olvidadas, porque nos enseñan quiénes somos. (Legal)",
                    3 to "Creatividad. El mundo necesita nuevas ideas y acciones audaces. (Caótico)",
                    4 to "Avaricia. Solo estoy en esto por el dinero y la fama. (Maligno)",
                    5 to "Gente. Me gusta ver las sonrisas en el rostro de las personas cuando actúo. Eso es todo lo que importa. (Neutral)",
                    6 to "Honestidad. El arte debe reflejar el alma; debe venir de dentro y revelar quiénes somos realmente. (Cualquiera)"
                ), links = mapOf(
                    1 to "Mi instrumento es mi posesión más preciada, y me recuerda a alguien a quien amo.",
                    2 to "Alguien robó mi preciado instrumento, y algún día lo recuperaré.",
                    3 to "Quiero ser famoso, cueste lo que cueste.",
                    4 to "Idolatro a un héroe de los viejos cuentos y mido mis acciones contra las de esa persona.",
                    5 to "Haré cualquier cosa para demostrar que soy superior a mi rival odiado.",
                    6 to "Haría cualquier cosa por los otros miembros de mi antigua compañía."
                ), flaws = mapOf(
                    1 to "Haré cualquier cosa para ganar fama y renombre.",
                    2 to "Soy un iluso cuando se trata de un rostro bonito.",
                    3 to "Un escándalo me impide volver a casa. Ese tipo de problemas parece seguirme.",
                    4 to "Una vez satiricé a un noble que aún quiere mi cabeza. Fue un error que probablemente repetiré.",
                    5 to "Tengo problemas para ocultar mis verdaderos sentimientos. Mi lengua afilada me mete en problemas.",
                    6 to "A pesar de mis mejores esfuerzos, soy poco confiable para mis amigos."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Artesano Gremial",
            competencies = "Perspicacia, Persuasión",
            tools = "Un tipo de herramientas de artesano.",
            languages = 1,
            items = "Herramientas de artesano (un tipo a tu elección), carta de presentación de tu gremio, muda de ropas de viaje y una bolsa con 15 po. ",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Creo que si se hace algo, hay que hacerlo bien. No puedo evitarlo, soy un perfeccionista.",
                    2 to "Soy un esnob que desprecia a los que no saben apreciar el arte.",
                    3 to "Siempre quiero entender cómo funcionan las cosas y qué mueve a las personas.",
                    4 to "Se me llena la boca de aforismos ocurrentes y conozco un refrán para cada ocasión.",
                    5 to "Soy maleducado con los que carecen de mi compromiso con el trabajo duro y el juego limpio.",
                    6 to "Me encanta hablar de mi profesión.",
                    7 to "No me desprendo de mi dinero con facilidad y regatearé incansablemente hasta conseguir el mejor trato posible.",
                    8 to "Soy famoso por mi trabajo y quiero asegurarme de que todo el mundo lo aprecia. Me quedo de piedra cuando alguien no ha oído hablar de mí."
                ), ideals = mapOf(
                    1 to "Comunidad. El deber de todos los pueblos desarrollados es fortalecer los lazos de la comunidad y proteger la civilización. (Legal)",
                    2 to "Generosidad. Recibí mis talentos para poder mejorar el mundo con ellos. (Bueno)",
                    3 to "Libertad. Todos deberían ser libres de dedicarse a su propio sustento. (Caótico)",
                    4 to "Codicia. Solo hago esto por el dinero. (Malvado)",
                    5 to "Personas. Mi compromiso es con la gente que me importa, no con ningún ideal. (Neutral)",
                    6 to "Aspiración. Trabajo enconadamente para ser el mejor en lo que hago. (Cualquiera)"
                ), links = mapOf(
                    1 to "El taller en el que aprendí mi oficio es el lugar más importante del mundo para mí.",
                    2 to "Creé un gran trabajo para alguien, pero luego descubrí que no era digno de recibirlo. Todavía estoy buscando a una persona que lo merezca.",
                    3 to "Estoy en deuda con mi gremio, pues me ha convertido en quien soy.",
                    4 to "Busco enriquecerme para atraer el amor de cierta persona.",
                    5 to "Algún día volveré a mi gremio y demostraré a todos que soy mejor artesano que cualquiera de ellos.",
                    6 to "Me vengaré de las fuerzas del mal que destruyeron mi negocio y me dejaron sin forma de ganarme la vida."
                ), flaws = mapOf(
                    1 to "Haré cualquier cosa para hacerme con algo raro o de valor incalculable.",
                    2 to "Pienso inmediatamente que están intentando timarme.",
                    3 to "Nadie debe descubrir jamás que robé dinero de las arcas del gremio.",
                    4 to "Nunca estoy satisfecho con lo que tengo. Siempre quiero más.",
                    5 to "Mataría por obtener un título nobiliario.",
                    6 to "Siento unos celos horribles de cualquiera que pueda eclipsar mis obras. Allá donde voy me rodean los rivales."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Charlatán",
            competencies = "Engaño, Juego de Manos",
            tools = "Útiles para disfrazarse, útiles para falsificar ",
            languages = NULL,
            items = "Muda de ropas de calidad, útiles para disfrazarse, herramientas para un timo de tu elección (diez botellas con tapones de corcho llenas de un líquido coloreado, un juego de dados trucados, una baraja de naipes marcada, un anillo de sellar de un duque imaginario) y una bolsa con 15 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Me enamoro y desenamoro con facilidad. Siempre ando detrás de alguien.",
                    2 to "Conozco una broma para cada ocasión, especialmente aquellas en las que el humor no resulta apropiado.",
                    3 to "Los halagos son mi herramienta preferida para conseguir lo que quiero.",
                    4 to "Soy un jugador nato, que no puede resistirse a los riesgos si van acompañados de una posible recompensa.",
                    5 to "Miento sobre casi todo, incluso cuando no hay una buena razón para ello.",
                    6 to "El sarcasmo y los insultos son mis armas favoritas.",
                    7 to "Llevo encima varios símbolos sagrados, invocando a la deidad que me convenga según las circunstancias.",
                    8 to "Me guardo todo aquello que pueda valer algo."
                ), ideals = mapOf(
                    1 to "Independencia. Soy un espíritu libre, nadie me dice lo que tengo que hacer. (Caótico)",
                    2 to "Justicia. Nunca me aprovecho de nadie que no pueda permitirse perder unas pocas monedas. (Legal)",
                    3 to "Caridad. Reparto el dinero que consigo entre aquellos que verdaderamente lo necesitan. (Bueno)",
                    4 to "Creatividad. Nunca repito la misma estafa. (Caótico)",
                    5 to "Amistad. Las posesiones vienen y se van, pero los lazos de amistad son para siempre. (Bueno)",
                    6 to "Aspiración. Estoy decidido a demostrar mi valía. (Cualquiera)"
                ), links = mapOf(
                    1 to "Desplumé a la persona equivocada y ahora debo asegurarme de que nunca vuelve a cruzarse conmigo ni con mis seres queridos.",
                    2 to "Todo se lo debo a mi mentor: una persona horrible que probablemente esté pudriéndose en alguna celda.",
                    3 to "En algún lugar hay un hijo mío que no me conoce. Conseguiré que viva en un mundo mejor.",
                    4 to "Provengo de una familia noble y algún día arrancaré mi título y tierras de las garras de los que me los robaron.",
                    5 to "Un poderoso mató a alguien a quien amaba. Pronto tendré mi venganza.",
                    6 to "Estafé y arruiné a una persona que no se lo merecía. Aunque busco redimirme de mis fechorías, jamás podré perdonarme a mí mismo."
                ), flaws = mapOf(
                    1 to "No puedo resistirme a una cara bonita.",
                    2 to "Siempre estoy en deuda. Gasto mis ilícitas ganancias en lujos decadentes más deprisa de lo que puedo conseguirlas.",
                    3 to "Estoy convencido de que nadie podría engañarme como yo engaño a los demás.",
                    4 to "Soy demasiado codicioso para mi propio bien. No puedo evitar arriesgarme si hay dinero de por medio.",
                    5 to "No puedo resistirme a estafar a los que son más poderosos que yo.",
                    6 to "Odio admitirlo y me detesto por ello, pero si las cosas se ponen serias huiré para preservar mi propia vida."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Criminal",
            competencies = "Engaño, Sigilo",
            tools = "Un tipo de juego a tu elección, herramientas de ladrón. ",
            languages = NULL,
            items = "Palanqueta, muda de ropas corrientes de color oscuro y con capucha, una bolsa con 15 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Siempre tengo un plan preparado para cuando las cosas salen mal.",
                    2 to "Siempre estoy tranquilo, independientemente de las circunstancias. Nunca levanto la voz ni dejo que las emociones me controlen.",
                    3 to "Lo primero que hago cuando llego a un sitio nuevo es identificar todos los objetos de valor... y dónde podría esconderlos.",
                    4 to "Prefiero hacer amigos que enemigos.",
                    5 to "Cuesta muchísimo ganarse mi confianza. Los que parecen más honestos suelen ser los que más tienen que esconder.",
                    6 to "No presto atención a los riesgos. Nunca me digas qué es probable y qué no.",
                    7 to "La mejor forma de que haga algo es decirme que no puedo hacerlo.",
                    8 to "Hasta el insulto más nimio me saca de mis casillas."
                ), ideals = mapOf(
                    1 to "Honor. No robo a mis compañeros de oficio. (Legal)",
                    2 to "Libertad. Las cadenas están para ser rotas, al igual que los que las forjan. (Caótico)",
                    3 to "Caridad. Robo a los ricos para ayudar a los que lo necesitan. (Bueno)",
                    4 to "Codicia. Haré lo que sea necesario para amasar una fortuna. (Malvado)",
                    5 to "Personas. Soy leal a mis amigos, no a un ideal y, por lo que a mí respecta, todos los demás pueden hacer la travesía del río Estigio. (Neutral)",
                    6 to "Redención. Hay una chispa de bondad en todo el mundo. (Bueno)"
                ), links = mapOf(
                    1 to "Estoy intentando saldar una deuda contraída con un generoso benefactor.",
                    2 to "Mis ganancias ilícitas van dirigidas a mantener a mi familia.",
                    3 to "Me quitaron algo importante y pienso robarlo de vuelta.",
                    4 to "Me convertiré en el ladrón más grande que haya existido jamás.",
                    5 to "Cometí un crimen terrible. Espero poder redimirme algún día.",
                    6 to "Alguien a quien amaba murió por culpa de un fallo que cometí. No volverá a suceder."
                ), flaws = mapOf(
                    1 to "Cuando veo algo valioso solo puedo pensar en robarlo.",
                    2 to "Si tengo que elegir entre el dinero y mis amigos, suelo quedarme con el dinero.",
                    3 to "Si hay un plan, se me olvidará. Y si no se me olvida, lo ignoraré.",
                    4 to "Tengo un tic que revela cuándo miento.",
                    5 to "Me daré la vuelta y huiré si las cosas se ponen feas.",
                    6 to "Un inocente está en la cárcel por un crimen que yo cometí. Y no me importa."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Ermitaño",
            competencies = "Medicina, Religión",
            tools = "Útiles de herborista",
            languages = 1,
            items = "Estuche para pergaminos lleno de notas de tus estudios u oraciones, manta para el invierno, muda de ropas comunes, útiles de herborista y 5 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "He estado tanto tiempo aislado que rara vez pronuncio palabra alguna, prefiriendo gesticular y proferir algún gruñido ocasional.",
                    2 to "Siempre me mantengo impertérrito, incluso en medio de un desastre.",
                    3 to "El líder de mi comunidad tenía una opinión para cada tema y estoy deseoso de compartir su sabiduría.",
                    4 to "Siento una empatía tremenda por los que sufren.",
                    5 to "No tengo ni la más remota idea de etiqueta o cómo comportarme en sociedad.",
                    6 to "Todo lo que sucede está conectado conmigo, como parte de un gran plan cósmico.",
                    7 to "Tiendo a perderme en mis propios pensamientos y meditaciones, ajeno a lo que me rodea.",
                    8 to "Estoy trabajando en una gran teoría filosófica y adoro compartir mis ideas."
                ), ideals = mapOf(
                    1 to "Bien mayor. Mis dones deben ser compartidos con todos, no limitarse a mi propio beneficio. (Bueno)",
                    2 to "Lógica. Las emociones no deben nublar nuestro sentido del bien y del mal ni nuestra capacidad para razonar de forma lógica. (Legal)",
                    3 to "Pensamiento libre. La curiosidad y el hacerse preguntas son los pilares del progreso. (Caótico)",
                    4 to "Poder. La soledad y la contemplación son el camino hacia un poder místico o mágico. (Malvado)",
                    5 to "Vive y deja vivir. Inmiscuirse en los asuntos de los demás solo trae problemas. (Neutral)",
                    6 to "Conocimiento de uno mismo. Si te conoces a ti mismo, no necesitarás conocer nada más. (Cualquiera)"
                ), links = mapOf(
                    1 to "Nada es más importante que el resto de miembros de mi ermita, orden o asociación.",
                    2 to "Me recluí para esconderme de aquellos que aún podrían estar buscándome. Algún día tendré que encararme con ellos.",
                    3 to "Todavía estoy buscando la iluminación que perseguía durante mi aislamiento, pues aún no he dado con ella.",
                    4 to "Me recluí porque amaba a quien nunca podría tener.",
                    5 to "Si mi descubrimiento saliera a la luz, podría traer la ruina al mundo.",
                    6 to "Mi retiro me ha dado un gran conocimiento sobre un terrible mal, que solo yo puedo destruir."
                ), flaws = mapOf(
                    1 to "Ahora que he vuelto al mundo, disfruto demasiado de sus placeres.",
                    2 to "Albergo pensamientos oscuros y sanguinarios, que mi aislamiento no consiguió apagar.",
                    3 to "Soy inflexible en mis pensamientos y filosofía.",
                    4 to "Dejo que mi necesidad de ganar discusiones se interponga entre mis amistades (o mi tranquilidad) y yo.",
                    5 to "Arriesgaría demasiado a cambio de un fragmento de conocimiento perdido.",
                    6 to "Me gusta guardar secretos y no los compartiré con nadie."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Erudito",
            competencies = "Conocimiento Arcano, Historia",
            tools = "",
            languages = 2,
            items = "Botella de tinta negra, pluma, cuchillo pequeño, carta de un colega muerto que te plantea una pregunta que aún no e res capaz de responder, muda de ropas comunes y una bolsa con 10 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Empleo polisílabos para demostrar mi erudición.",
                    2 to "He leído todos y cada uno de los libros de las bibliotecas más grandes del mundo. O al menos me gusta presumir de haberlo hecho.",
                    3 to "Estoy acostumbrado a ayudar a los que no son tan listos como yo, por lo que explico todo lo explicable con paciencia infinita.",
                    4 to "Nada me gusta más que un buen misterio.",
                    5 to "Estoy dispuesto a escuchar las dos posturas de todas las discusiones antes de emitir mi propio juicio.",
                    6 to "Pronuncio... despacio... cuando hablo... con idiotas,... como... la mayoría... de gente... que... no... soy... yo.",
                    7 to "Soy torpe hasta extremos increíbles en situaciones sociales.",
                    8 to "Estoy convencido de que los demás siempre están intentando robarme mis secretos."
                ), ideals = mapOf(
                    1 to "Conocimiento. El camino hacia el poder y la mejora de uno mismo pasa por el conocimiento. (Neutral)",
                    2 to "Belleza. Lo bello conduce indefectiblemente a lo verdadero. (Bueno)",
                    3 to "Lógica. Las emociones no deben nublar nuestra capacidad para razonar de forma lógica. (Legal)",
                    4 to "Ausencia de límites. Nada debería poner grilletes a las infinitas posibilidades inherentes a la propia existencia. (Caótico)",
                    5 to "Poder. El conocimiento es la senda que conduce hacia el poder y el control. (Malvado)",
                    6 to "Mejora personal. El objeto de una vida de estudio es mejorarse a uno mismo. (Cualquiera)"
                ), links = mapOf(
                    1 to "Es mi deber proteger a mis estudiantes.",
                    2 to "Poseo un texto ancestral que guarda secretos terribles. No debe caer en malas manos.",
                    3 to "Trabajo para proteger una biblioteca, universidad, scriptorium o monasterio.",
                    4 to "El trabajo de mi vida es una serie de volúmenes relativos a un campo del saber concreto.",
                    5 to "Me he pasado la vida buscando la respuesta a una pregunta.",
                    6 to "He vendido mi alma a cambio de conocimiento. Aspiro a hacer grandes hazañas y recuperarla."
                ), flaws = mapOf(
                    1 to "Las promesas de información me distraen con facilidad.",
                    2 to "La mayoría de la gente gritaría y saldría corriendo si viera un demonio. Yo me paro y tomo apuntes sobre su anatomía.",
                    3 to "Desentrañar un misterio antiguo bien vale una civilización.",
                    4 to "Suelo pasar por alto soluciones obvias al obcecarme con otras más complicadas.",
                    5 to "Abro la boca sin pensar lo que voy a decir, por lo que suelo acabar insultando a los demás.",
                    6 to "No podría guardar un secreto ni aunque mi vida (o la de otros) dependiera de ello."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Héroe del pueblo",
            competencies = "Supervivencia, Trato con Animales",
            tools = "Un tipo de herramientas de artesano, vehículos terrestres",
            languages = NULL,
            items = "Herramientas de artesano (un tipo a tu elección), pala, olla de h ierro, muda de ropas comunes y una bolsa con 10 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Juzgo a los demás por sus actos, no por sus palabras.",
                    2 to "Si alguien está en peligro, no dudo en acudir en su ayuda.",
                    3 to "Cuando me decido a hacer algo nada puede interponerse en mi camino.",
                    4 to "Tengo un sentido de la justicia muy desarrollado, por lo que siempre intento encontrar la solución más equitativa a los conflictos.",
                    5 to "Confío en mis propias habilidades y hago lo que puedo para transmitir esa confianza a los demás.",
                    6 to "Pensar es para otros. Yo prefiero actuar.",
                    7 to "Abuso de los polisílabos para parecer más listo.",
                    8 to "Me aburro con facilidad. ¿Cuándo puedo ponerme manos a la obra con mi destino?"
                ), ideals = mapOf(
                    1 to "Respeto. La gente merece ser tratada con dignidad y respeto. (Bueno)",
                    2 to "Justicia. Nadie debería recibir un trato preferencial ante la ley. Nadie está por encima de ella. (Legal)",
                    3 to "Libertad. No se debe permitir que los tiranos opriman al pueblo. (Caótico)",
                    4 to "Fuerza. Si me hago fuerte, podré tomar lo que quiero ... lo que merezco. (Malvado)",
                    5 to "Sinceridad. No es bueno pretender ser quien no eres. (Neutral)",
                    6 to "Destino. Nada ni nadie podrá apartarme de mi gran misión. (Cualquiera)"
                ), links = mapOf(
                    1 to "Tengo una familia, pero no sé dónde están. Espero poder volver a verlos alguna vez.",
                    2 to "Trabajé la tierra, amo la tierra y protegeré la tierra.",
                    3 to "Un noble orgulloso me propinó una paliza terrible y desde entonces me cobro mi venganza con todo abusón con el que me cruzo.",
                    4 to "Mis herramientas son los símbolos de mi pasado. Las llevo siempre conmigo para no olvidar mis raíces.",
                    5 to "Protejo a aquellos incapaces de protegerse a sí mismos.",
                    6 to "Ojalá mi amor de la infancia me hubiera acompañado en la búsqueda de mi destino."
                ), flaws = mapOf(
                    1 to "El tirano que gobierna mi tierra no se detendrá ante nada para verme muerto.",
                    2 to "Estoy convencido de la importancia de mi destino, haciendo caso omiso de mis defectos o la posibilidad de fracasar.",
                    3 to "Los que me conocen desde niño son partícipes de mi vergonzoso secreto, así que nunca podré volver a mi hogar.",
                    4 to "Siento debilidad por los vicios de la ciudad, en especial la bebida.",
                    5 to "En mi fuero interno pienso que las cosas irían mejor si un tirano gobernara la región.",
                    6 to "Me cuesta confiar en mis aliados."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Huerfano",
            competencies = "Juego de Manos, Sigilo",
            tools = "Herramientas de ladrón, útiles para disfrazarse",
            languages = NULL,
            items = "Cuchillo pequeño, mapa de la ciudad en la que creciste, ratón (tu mascota), recuerdo de tus padres, muda de ropas comunes y una bolsa con 10 po. ",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Escondo pedazos de comida y bagatelas en mis bolsillos.",
                    2 to "Hago un montón de preguntas.",
                    3 to "Me gusta colarme en huecos pequeños, en los que nadie puede atraparme.",
                    4 to "Duermo con la espalda contra la pared o un árbol, y todas mis pertenencias están envueltas en un saco al que me abrazo con fuerza.",
                    5 to "Como como un cerdo y tengo unos modales terribles.",
                    6 to "Pienso que todo el que se porta bien conmigo posee malas intenciones.",
                    7 to "No me gusta bañarme.",
                    8 to "No me corto al decir lo que otros insinúan u ocultan."
                ), ideals = mapOf(
                    1 to "Respeto. Todo el mundo, pobre o rico, merece respeto (Bueno).",
                    2 to "Comunidad. Tenemos que cuidar los unos de los otros, porque nadie más va a hacerlo (Legal).",
                    3 to "Cambio. Los que están abajo deben ser elevados y los que están en lo alto derribados. El cambio es la naturaleza de las cosas (Caótico).",
                    4 to "Castigo. Debemos mostrar a los ricos cómo se vive y muere en las alcantarillas (Malvado).",
                    5 to "Personas. Ayudo a quienes me ayudan. Eso es lo que nos mantiene con vida (Neutral).",
                    6 to "Aspiración. Demostraré que merezco una vida mejor (Cualquiera)."
                ), links = mapOf(
                    1 to "Mi ciudad es mi hogar y lucharé para defenderla.",
                    2 to "Patrocino un orfanato que evita que otros padezcan lo que yo tuve que sufrir.",
                    3 to "Debo mi supervivencia a otro huérfano, que me enseñó a vivir en la calle.",
                    4 to "Tengo una deuda que jamás podré pagar con la persona que se apiadó de mí.",
                    5 to "Puse fin a mi vida de pobreza robando a una persona importante y ahora se me busca por ello.",
                    6 to "Nadie debería tener que soportar las dificultades por las que he pasado."
                ), flaws = mapOf(
                    1 to "Si me superan en número en una pelea, huiré.",
                    2 to "Me parece que el oro vale mucho dinero, así que haré todo lo que sea necesario para conseguir más.",
                    3 to "Nunca confiaré por completo en nadie que no sea yo mismo.",
                    4 to "Prefiero matar a alguien mientras duerme que enfrentarme a él en una pelea justa.",
                    5 to "Si lo necesitas más que el otro, no cuenta como robar.",
                    6 to "Los que no pueden cuidarse a sí mismos tienen lo que se merecen."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Marinero",
            competencies = "Atletismo, Percepción",
            tools = "herramientas de navegante, vehículos acuáticos",
            languages = NULL,
            items = "Cabilla (garrote), 50 pies de cuerda de seda, amuleto de la suerte como una pata de conejo o una piedra pequeña con un agujero en el centro (o puedes tirar en la tabla \"bagatelas\" del capítulo 5), muda de ropas comunes y una bolsa con 10 po. ",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Mis amigos saben que, pase lo que pase, pueden confiar en mí.",
                    2 to "Trabajo duro para poder disfrutar al máximo al terminar.",
                    3 to "Me encanta arribar a puertos nuevos y hacer amigos junto a una jarra de cerveza.",
                    4 to "Retuerzo ligeramente la verdad en aras de una historia interesante.",
                    5 to "Una trifulca de taberna es una forma maravillosa de conocer una ciudad nueva.",
                    6 to "Nunca paso por alto una apuesta hecha en buena fe.",
                    7 to "Mi lenguaje es tan desagradable como el nido de un otyugh.",
                    8 to "Aprecio un trabajo bien hecho, en especial si puedo convencer a otro para hacerlo."
                ), ideals = mapOf(
                    1 to "Respeto. Lo que mantiene a una nave unida es el respeto mutuo entre capitán y tripulación (Bueno).",
                    2 to "Justicia. Todos trabajamos, así que todos compartimos la recompensa (Legal).",
                    3 to "Libertad. El mar es libertad. Libertad para ir a donde me plazca y hacer lo que quiera (Caótico).",
                    4 to "Dominio. Soy un depredador y el resto de barcos no son sino mis presas (Malvado).",
                    5 to "Personas. Estoy comprometido con mis compañeros de tripulación, no con un ideal (Neutral).",
                    6 to "Aspiración. Algún día tendré mi propio barco y trazaré mi propio destino (Cualquiera)."
                ), links = mapOf(
                    1 to "Mi lealtad es para mi capitán, todo lo demás va después.",
                    2 to "Capitán y compañeros vienen y se van. La nave es lo más importante.",
                    3 to "Siempre recordaré mi primer barco.",
                    4 to "En uno de los puertos que he visitado me espera un amante, cuyos ojos casi me apartan del mar.",
                    5 to "Me estafaron la parte del botín que me correspondía y pienso recuperar lo que se me debe.",
                    6 to "Unos piratas despiadados asesinaron a mi capitán y al resto de la tripulación, saquearon nuestra nave y me dejaron morir. La venganza será mía."
                ), flaws = mapOf(
                    1 to "Obedezco órdenes, incluso cuando pienso que están equivocadas.",
                    2 to "Diré lo que sea para evitar hacer trabajo de más.",
                    3 to "Nunca me echo atrás si alguien cuestiona mi valor, sin importar el peligro de la situación.",
                    4 to "Cuando empiezo a beber me cuesta muchísimo parar.",
                    5 to "No puedo evitar embolsarme las monedas y otros abalorios que me encuentro.",
                    6 to "Mi orgullo acabará siendo mi perdición."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Noble",
            competencies = "Historia, Persuasión",
            tools = "Un tipo de juego a tu elección",
            languages = 1,
            items = "Muda de ropas de calidad, anillo de sellar, documento que acredita el linaje y un monedero con 25 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Mis elocuentes halagos hacen que todo aquel con el que hablo se sienta la persona más maravillosa e importante del mundo.",
                    2 to "El pueblo llano me adora por mi bondad y generosidad.",
                    3 to "Nadie que observara mi regio porte podría dudar que estoy muy por encima del populacho.",
                    4 to "Me tomo muchas molestias en estar siempre presentable y a la última moda.",
                    5 to "No me gusta ensuciarme, y bajo ningún concepto me alojaré en dependencias inapropiadas a mi alcurnia.",
                    6 to "A pesar de mi origen noble no me considero por encima de los demás. Todos tenemos la misma sangre.",
                    7 to "Quien pierde mi favor, lo pierde para siempre.",
                    8 to "Si me causas perjuicio alguno, te aplastaré, arruinaré tu nombre y echaré sal en tus tierras."
                ), ideals = mapOf(
                    1 to "Respeto. Se me debe respeto por mi posición, pero todos, independientemente de su cuna, merecen ser tratados con dignidad (Bueno).",
                    2 to "Responsabilidad. Es mi deber respetar la autoridad de los que están por encima de mí, igual que aquellos de posición inferior deben respetarme a mí (Legal).",
                    3 to "Independencia. Debo demostrar que puedo valerme por mí mismo, sin la protección de mi familia (Caótico).",
                    4 to "Poder. Si consigo más poder, nadie me dirá lo que tengo que hacer (Malvado).",
                    5 to "Familia. La sangre tira. La familia es lo primero (Cualquiera).",
                    6 to "Deber de la nobleza. Es mi deber proteger y cuidar de aquellos bajo mi cargo (Bueno)."
                ), links = mapOf(
                    1 to "Me enfrentaré a cualquier desafío para obtener la aprobación de mi familia.",
                    2 to "La alianza de mi casa con otra familia noble debe ser mantenida, cueste lo que cueste.",
                    3 to "Nada es más importante que el resto de miembros de mi familia.",
                    4 to "Amo secretamente al heredero de una familia despreciada por la mía.",
                    5 to "Mi lealtad hacia el soberano es inquebrantable.",
                    6 to "El pueblo llano debe verme como su héroe."
                ), flaws = mapOf(
                    1 to "Aunque no lo confesaré, pienso que todos están por debajo de mí.",
                    2 to "Escondo un secreto realmente escandaloso, que podría arruinar a mi familia para siempre.",
                    3 to "Percibo insultos velados y amenazas en todas y cada una de las palabras que me dirigen. Además, me enfado rápidamente.",
                    4 to "Poseo un deseo insaciable por los placeres de la carne.",
                    5 to "Es un hecho, el mundo gira en torno a mí.",
                    6 to "Mis palabras y mis actos suelen traer la vergüenza a mi familia."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Salvaje",
            competencies = "Atletismo, Supervivencia",
            tools = "Un tipo de instrumento musical",
            languages = 1,
            items = "Bastón, trampa para cazar, trofeo de un animal al que has matado, muda de ropas de viaje y una bolsa con 10 po. ",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Mis elocuentes halagos hacen que todo aquel con el que hablo se sienta la persona más maravillosa e importante del mundo.",
                    2 to "El pueblo llano me adora por mi bondad y generosidad.",
                    3 to "Nadie que observara mi regio porte podría dudar que estoy muy por encima del populacho.",
                    4 to "Me tomo muchas molestias en estar siempre presentable y a la última moda.",
                    5 to "No me gusta ensuciarme, y bajo ningún concepto me alojaré en dependencias inapropiadas a mi alcurnia.",
                    6 to "A pesar de mi origen noble no me considero por encima de los demás. Todos tenemos la misma sangre.",
                    7 to "Quien pierde mi favor, lo pierde para siempre.",
                    8 to "Si me causas perjuicio alguno, te aplastaré, arruinaré tu nombre y echaré sal en tus tierras."
                ), ideals = mapOf(
                    1 to "Respeto. Se me debe respeto por mi posición, pero todos, independientemente de su cuna, merecen ser tratados con dignidad (Bueno).",
                    2 to "Responsabilidad. Es mi deber respetar la autoridad de los que están por encima de mí, igual que aquellos de posición inferior deben respetarme a mí (Legal).",
                    3 to "Independencia. Debo demostrar que puedo valerme por mí mismo, sin la protección de mi familia (Caótico).",
                    4 to "Poder. Si consigo más poder, nadie me dirá lo que tengo que hacer (Malvado).",
                    5 to "Familia. La sangre tira. La familia es lo primero (Cualquiera).",
                    6 to "Deber de la nobleza. Es mi deber proteger y cuidar de aquellos bajo mi cargo (Bueno)."
                ), links = mapOf(
                    1 to "Me enfrentaré a cualquier desafío para obtener la aprobación de mi familia.",
                    2 to "La alianza de mi casa con otra familia noble debe ser mantenida, cueste lo que cueste.",
                    3 to "Nada es más importante que el resto de miembros de mi familia.",
                    4 to "Amo secretamente al heredero de una familia despreciada por la mía.",
                    5 to "Mi lealtad hacia el soberano es inquebrantable.",
                    6 to "El pueblo llano debe verme como su héroe."
                ), flaws = mapOf(
                    1 to "Aunque no lo confesaré, pienso que todos están por debajo de mí.",
                    2 to "Escondo un secreto realmente escandaloso, que podría arruinar a mi familia para siempre.",
                    3 to "Percibo insultos velados y amenazas en todas y cada una de las palabras que me dirigen. Además, me enfado rápidamente.",
                    4 to "Poseo un deseo insaciable por los placeres de la carne.",
                    5 to "Es un hecho, el mundo gira en torno a mí.",
                    6 to "Mis palabras y mis actos suelen traer la vergüenza a mi familia."
                )
            )
        ), Background(
            backgroundId = NULL,
            bgName = "Soldado",
            competencies = "Atletismo, Intimidación",
            tools = "Un tipo de juego a tu elección, vehículos terrestres",
            languages = NULL,
            items = "Insignia de tu rango, trofeo tomado de un enemigo muerto (una daga , un filo roto o un pedazo de tela de un estandarte), juego de dados o baraja de cartas, muda de ropas comunes y una bolsa con 10 po.",
            traits = Traits(
                personalityTraits = mapOf(
                    1 to "Siempre soy educado y respetuoso.",
                    2 to "Me persiguen los horribles recuerdos de la guerra. No puedo sacarme de la cabeza las visiones de violencia.",
                    3 to "He perdido demasiados amigos y me cuesta hacer otros nuevos.",
                    4 to "Conozco historias inspiradoras y admonitorias, sacadas de mis experiencias como militar, para cualquier situación.",
                    5 to "Puedo mirar fijamente a un sabueso infernal sin amilanarme.",
                    6 to "Me encanta ser fuerte y romper cosas.",
                    7 to "Tengo un sentido del humor muy vulgar.",
                    8 to "Encaro los problemas de frente. Una solución sencilla y directa es el mejor camino hacia el éxito."
                ), ideals = mapOf(
                    1 to "Bien mayor. Nuestro deber es arriesgar nuestras vidas para defender a otros (Bueno).",
                    2 to "Responsabilidad. Hago lo que debo y obedezco a la autoridad cuando esta es justa (Legal).",
                    3 to "Independencia. Cuando se obedecen órdenes sin pensar se abraza una forma de tiranía (Caótico).",
                    4 to "Fuerza. En la guerra, como en la vida, el más fuerte triunfa (Malvado).",
                    5 to "Vive y deja vivir. No merece la pena ir a la guerra o matar por un ideal (Neutral).",
                    6 to "Nación. Mi ciudad, mi nación o mi gente es lo único que importa (Cualquiera)."
                ), links = mapOf(
                    1 to "Daría mi vida por aquellos con los que he servido.",
                    2 to "Alguien me salvó la vida en el campo de batalla. Nunca he dejado ni dejaré a un amigo atrás.",
                    3 to "Mi honor es mi vida.",
                    4 to "Nunca olvidaré la demoledora derrota que mi compañía sufrió o los enemigos que nos la infligieron.",
                    5 to "Los que luchan a mi lado son aquellos por los que merece la pena morir.",
                    6 to "Combato por aquellos incapaces de combatir por sí mismos."
                ), flaws = mapOf(
                    1 to "El monstruoso enemigo con el que nos enfrentamos en batalla aún me hace estremecer de terror.",
                    2 to "Tengo poco respeto por quien no haya demostrado ser un guerrero.",
                    3 to "Durante una batalla cometí un error terrible, que costó numerosas vidas, y haría lo que fuera para mantenerlo en secreto.",
                    4 to "Mi odio hacia el enemigo es ciego e irracional.",
                    5 to "Siempre obedezco la ley, incluso cuando hacerlo trae miseria.",
                    6 to "Preferiría tragarme mi armadura antes que admitir que me he equivocado."
                )
            )
        )
    )
    for (backgroundObj in backgrounds) {

        val gson = Gson()
        val traitsJson = gson.toJson(backgroundObj.traits)

        val values = ContentValues().apply {
            put("bg_name", backgroundObj.bgName)
            put("competencies", backgroundObj.competencies)
            put("tools", backgroundObj.tools)
            put("languages", backgroundObj.languages)
            put("items", backgroundObj.items)
            put("rasgos", traitsJson)
        }
        db.insert("backgrounds", null, values)
    }
}

private fun insertClasses(db: SQLiteDatabase) {
    val classes = arrayOf(
        Classe(
            classId = NULL,
            className = "Barbaro",
            hitDie = "1d12",
            savingThrowProficiencies = "Fuerza, Constitucion",
            abilitiesProficiencies = "Atletismo, Intimidación, Naturaleza, Percepción, Supervivencia y Trato con Animales. ",
            armorWeaponProficiencies = "Armadura: armadura ligeras y medias, escudos. Armas: armas sencillas y marciales, Herramientas: ninguna"
        ),
        Classe(
            classId = NULL,
            className = "Bardo",
            hitDie = "1d8",
            savingThrowProficiencies = "Destreza, Carisma",
            abilitiesProficiencies = "3 cualesquiera",
            armorWeaponProficiencies = "Armadura: armaduras ligeras. Armas: armas sencillas, ballestas de mano, espadas cortas, espadas largas y estoques. Herramientas: tres instrumentos musicales a tu elección."
        ),
        Classe(
            classId = NULL,
            className = "Brujo",
            hitDie = "1d8",
            savingThrowProficiencies = " Sabiduría, Carisma",
            abilitiesProficiencies = "Conocimiento Arcano Engaño, Historia, Intimidación, Investigación, Naturaleza y Religión.",
            armorWeaponProficiencies = "Armadura: armaduras ligeras. Armas: armas sencillas. Herramientas: ninguna."
        ),
        Classe(
            classId = NULL,
            className = "Clerigo",
            hitDie = "1d8",
            savingThrowProficiencies = "Sabiduría, Carisma",
            abilitiesProficiencies = " Historia, Medicina, Perspicacia, Persuasión y Religión",
            armorWeaponProficiencies = "Armadura: armaduras ligeras y medias, escudos. Armas: armas sencillas. Herramientas: ninguna."
        ),
        Classe(
            classId = NULL,
            className = "Druida",
            hitDie = "1d8",
            savingThrowProficiencies = "Inteligencia, Sabiduria",
            abilitiesProficiencies = "Conocimiento Arcano, Medicina, Naturaleza, Percepción, Pers picacia, Re ligión y Trato con Animales",
            armorWeaponProficiencies = "Armadura: armaduras ligeras, armaduras medias y escudos, aunque los druidas nunca llevan armaduras ni escudos hechos de metal. Armas: garrotes, dagas, dardos, jabalinas, mazas, bastones, cimitarras, hoces, hondas y lanzas. Herramientas: útiles de herborista."
        ),
        Classe(
            classId = NULL,
            className = "Explorador",
            hitDie = "1d10",
            savingThrowProficiencies = "Fuerza, Destreza",
            abilitiesProficiencies = "Atletismo, Investigación, Naturaleza, Percepción, Perspicacia, Sigilo, Supervivencia y Trato con Animales",
            armorWeaponProficiencies = "Armadura: armaduras ligeras y medias, escudos. Armas: armas sencillas y marcia les. Herramientas: ninguna"
        ),
        Classe(
            classId = NULL,
            className = "Guerrero",
            hitDie = "1d10",
            savingThrowProficiencies = "Fuerza, Constitución",
            abilitiesProficiencies = "Acrobacias, Atletismo, Historia, Intimidación, Percepción, Perspicacia, Supervivencia Trato con Animales",
            armorWeaponProficiencies = "Armadura: todas las armaduras y escudos. Armas: armas sencillas y marcia les. Herramientas: ninguna. "
        ),
        Classe(
            classId = NULL,
            className = "Hechicero",
            hitDie = "1d6",
            savingThrowProficiencies = " Constitución, Carisma",
            abilitiesProficiencies = "Conocimiento Arcano, Engaño, Intimidación, Perspicacia, Persuasión y Religión",
            armorWeaponProficiencies = "Armadura: ninguna. Armas: dagas, dardos, hondas, bastones y ballestas ligeras. Herramientas: ninguna."
        ),
        Classe(
            classId = NULL,
            className = "Mago",
            hitDie = "1d6",
            savingThrowProficiencies = "Inteligencia, Sabiduría",
            abilitiesProficiencies = "Conocimiento Arcano, Historia, Investigación, Medicina, Perspicacia y Religión",
            armorWeaponProficiencies = "Armadura: ninguna. Armas: dagas, dardos, hondas, bastones y ballestas ligeras. Herramientas: ninguna"
        ),
        Classe(
            classId = NULL,
            className = "Monje",
            hitDie = "1d8",
            savingThrowProficiencies = "Fuerza, Destreza",
            abilitiesProficiencies = "Acrobacias, Atletismo, Historia, Perspicacia, Religión y Sigilo",
            armorWeaponProficiencies = "Armadura: ninguna. Armas: armas sencillas, espadas cortas. Herramientas: escoge un tipo de herramientas de artesano o un instrumento musical."
        ),
        Classe(
            classId = NULL,
            className = "Paladin",
            hitDie = "1d10",
            savingThrowProficiencies = " Sabiduría, Carisma",
            abilitiesProficiencies = "Atletismo, Intimidación, Medicina, Perspicacia, Persuasión y Religión",
            armorWeaponProficiencies = "Armadura: todas las armaduras y escudos. Armas: armas sencillas y marciales. Herramientas: ninguna"
        ),
        Classe(
            classId = NULL,
            className = "Picaro",
            hitDie = "1d8",
            savingThrowProficiencies = "Destreza, Inteligencia",
            abilitiesProficiencies = "Acrobacias, Atletismo. Engaño, Interpretación. Intimidación, Investigación, Juego de Manos, Percepción, Perspicacia. Persuasión y Sigilo",
            armorWeaponProficiencies = "Armadura: armaduras ligeras. Armas: armas sencillas, ballestas de mano, espadas cortas. espadas largas y estoques. Herramientas: herramientas de ladrón. "
        ),

        )
    for (classObj in classes) {
        val values = ContentValues().apply {
            put("class_name", classObj.className)
            put("hit_die", classObj.hitDie)
            put("saving_throw_proficiencies", classObj.savingThrowProficiencies)
            put("habilities_proficiencies", classObj.abilitiesProficiencies)
            put("armor_weapon_proficiencies", classObj.armorWeaponProficiencies)
        }
        db.insert("classes", null, values)
    }
}

private fun insertLevel(db: SQLiteDatabase) {
    val levels = arrayOf(
        Level(
            levelId = NULL,
            classId = 1,
            level = 1,
            featureName = "Furia",
            description = "Cuando estás en medio de un combate, luchas con una ferocidad primordial. Durante tu turno, puedes usar tu acción adicional para dejarte llevar por la furia. Mientras estés enfurecido, y siempre que no lleves armadura pesada, obtendrás los siguientes beneficios: Tienes ventaja en las pruebas de Fuerza y las tiradas de salvación de Fuerza. Cuando haces un ataque con arma cuerpo a cuerpo utilizando Fuerza, ganas un bonificador a la tirada de daño que aumenta según subes de nivel como bárbaro, tal y como se indica en la columna \"daño por furia\" de la tabla del bárbaro. Posees resistencia al daño contundente, cortante y perforante. Si normalmente eras capaz de lanzar conjuros, no podrás lanzarlos ni concentrarte en ellos mientras estés enfurecido. Tu furia dura 1 minuto, aunque acabará antes si quedas inconsciente, o si terminas tu turno sin haber atacado a una criatura hostil o sin haber recibido daño desde tu turno anterior. Si lo deseas, también puedes finalizar tu furia empleando una acción adicional durante tu turno. Una vez te hayas enfurecido tantas veces como el número indicado en la columna \"n.º furias\" de la tabla del bárbaro, tendrás que llevar a cabo un descanso largo antes de poder dejarte llevar por la furia de nuevo."
        ), Level(
            levelId = 2, classId = 1, level = 1, featureName = "Número de furias", description = "2"
        ), Level(
            levelId = 3, classId = 1, level = 1, featureName = "Daño de furia", description = "2"
        ), Level(
            levelId = 4,
            classId = 1,
            level = 1,
            featureName = "Defensa sin armadura",
            description = "Si no estás portando armadura alguna, tu Clase de Armadura será 10 + tu modificador por Destreza + tu modificador por Constitución. Podrás usar escudo sin tener que renunciar a este beneficio."
        ), Level(
            levelId = 5,
            classId = 1,
            level = 2,
            featureName = "Ataque temerario",
            description = "A partir de nivel 2, puedes abandonar por completo tu defensa para atacar con una fiereza desesperada. Cuando vayas a realizar el primer ataque de cada turno, puedes decidir atacar temerariamente. Si eliges hacer esto, tendrás ventaja en las tiradas de ataque con armas cuerpo a cuerpo que utilicen Fuerza durante este turno, pero las tiradas de ataque que te tengan como objetivo hasta el final de tu siguiente turno también tendrán ventaja."
        ), Level(
            levelId = 6, classId = 1, level = 2, featureName = "Número de furias", description = "2"
        ), Level(
            levelId = 7, classId = 1, level = 2, featureName = "Daño por furia", description = "2"
        ), Level(
            levelId = 8,
            classId = 1,
            level = 2,
            featureName = "Sentir el peligro",
            description = "A nivel 2, eres capaz de percibir de forma casi sobrenatural cuándo lo que te rodea no es como debería ser, lo que te permite evitar el peligro eficazmente. Tienes ventaja en las tiradas de salvación de Destreza para evitar efectos que puedas ver, como trampas o conjuros. No obtendrás este beneficio si estás cegado, ensordecido o incapacitado."
        ), Level(
            levelId = 9,
            classId = 1,
            level = 3,
            featureName = "Senda primordial",
            description = "A nivel 3, debes escoger una senda que determinará la naturaleza de tu furia. Puedes elegir entre la Senda del Berserker y la Senda del Guerrero Totémico. Cada una de estas sendas se detalla al final de la descripción de esta clase. Esta elección te proporcionará ciertos rasgos cuando alcances los niveles 3, 6, 10 y 14."
        ), Level(
            levelId = 10,
            classId = 1,
            level = 3,
            featureName = "Número de furias",
            description = "3"
        ), Level(
            levelId = 11, classId = 1, level = 3, featureName = "Daño por furia", description = "2"
        ), Level(
            levelId = 15,
            classId = 2,
            level = 1,
            featureName = "Lanzamiento de conjuros",
            description = "Has aprendido a desenmarañar y remodelar el tejido de la realidad, en armonía con tus deseos y la música. Los conjuros que puedes lanzar son parte de tu vasto repertorio, una magia que afinas para adaptarte a diferentes situaciones. Las reglas de lanzamiento de conjuros y la lista de conjuros de bardo se encuentran en los capítulos 10 y 11 respectivamente. Trucos: Conoces dos trucos de tu elección de la lista de conjuros de bardo. A medida que avances de nivel, podrás elegir más trucos, como se indica en la columna \"trucos conocidos\" de la tabla del bardo. Espacios de Conjuro: La tabla del bardo muestra cuántos espacios de conjuro dispones para lanzar conjuros de nivel 1 y superiores. Para lanzar uno de estos conjuros, debes usar un espacio de al menos el nivel del conjuro. Recuperas todos los espacios utilizados tras un descanso largo. Conjuros Conocidos de Nivel 1 y Superiores: Inicias con cuatro conjuros de nivel 1 de tu elección de la lista de conjuros de bardo. Conforme subas de nivel, podrás aprender más conjuros de niveles para los que tengas espacios de conjuro disponibles. Aptitud Mágica: El Carisma es tu habilidad para lanzar conjuros de bardo, ya que tu magia se origina en el corazón y el alma que pones en tus interpretaciones. Usas tu Carisma para determinar la CD de las tiradas de salvación y las tiradas de ataque de tus conjuros de bardo. CD de tirada de salvación de conjuros = 8 + tu bonificador por competencia + tu modificador por Carisma  Modificador de ataque de conjuros = tu bonificador por competencia + tu modificador por Carisma  Lanzamiento Ritual: Puedes lanzar de forma ritual aquellos conjuros de bardo que conozcas y que estén marcados como \"ritual\". Canalizador Mágico: Puedes usar un instrumento musical como canalizador mágico para tus conjuros de bardo."
        ), Level(
            levelId = 16,
            classId = 2,
            level = 1,
            featureName = "Inspiración bardica",
            description = "Puedes recurrir a tus palabras o a tu música, especialmente emotivas, para inspirar a los demás. Para hacer esto, deberás utilizar una acción adicional durante tu turno y elegir a una criatura que no seas tú, pueda oírte y esté a 60 pies o menos de ti. Dicho objetivo recibe un dado de Inspiración Bárdica, 1d6. Una sola vez, antes de que pasen 10 minutos, la criatura puede tirar el dado y añadir el resultado a una de sus pruebas de característica, tiradas de ataque o tiradas de salvación. Puede esperar a ver el resultado del d20 antes de elegir si usar el dado de Inspiración Bárdica, pero debe tomar la decisión antes de que el DM diga si la tirada tiene éxito o no. Una vez se tira el dado de Inspiración Bárdica, este se pierde. Además, cada criatura solo puede tener un dado de Inspiración Bárdica al mismo tiempo. Puedes utilizar este rasgo tantas veces como tu modificador por Carisma (mínimo una vez). Recuperas todos los usos tras finalizar un descanso largo. Tu dado de Inspiración Bárdica cambia cuando alcanzas ciertos niveles de esta clase: pasa a ser 1d8 a nivel 5, 1d10 a nivel 10 y 1d12 a nivel 15."
        ), Level(
            levelId = 17,
            classId = 2,
            level = 2,
            featureName = "Aprendiz de mucho",
            description = "A partir de nivel 2, puedes añadir la mitad de tu bonificador por competencia (redondeado hacia abajo) a cualquier prueba de característica que hagas que no se beneficie ya de tu bonificador por competencia."
        ), Level(
            levelId = 18,
            classId = 2,
            level = 2,
            featureName = "Canción de descanso",
            description = "A partir de nivel 2, puedes usar palabras o canciones reconfortantes para ayudar a tus aliados heridos a recuperarse durante un descanso corto. Si cualquiera de las criaturas amistosas, incluyéndote a ti mismo, que escuchan tu interpretación gasta Dados de Golpe para recuperar puntos de golpe al final del descanso corto, cada una de ellas recobrará 1d6 puntos de golpe adicionales. Estos puntos de golpe adicionales aumentan cuando alcanzas ciertos niveles en esta clase: a 1d8 a nivel 9, a 1d10 a nivel 13, y a 1d12 a nivel 17."
        ), Level(
            levelId = 19,
            classId = 2,
            level = 3,
            featureName = "Colegio bárdico",
            description = "A nivel 3, profundizas en las técnicas avanzadas de un colegio bárdico a tu elección, ya sea el Colegio del Conocimiento o el Colegio del Valor. Cada uno de estos colegios está explicado al final de la descripción de la clase de bardo. Esta elección te otorga ciertos rasgos específicos cuando alcanzas los niveles 3, 6 y 14."
        ), Level(
            levelId = 20,
            classId = 2,
            level = 3,
            featureName = "Pericia",
            description = "A nivel 3, escoge dos habilidades en las que seas competente. Tu bonificador por competencia se duplica para cualquier prueba de característica que hagas utilizando cualquiera de las dos habilidades elegidas. A nivel 10 puedes elegir otras dos habilidades en las que seas competente, para que estas también disfruten de este beneficio."
        ), Level(
            levelId = 21,
            classId = 3,
            level = 1,
            featureName = "Patrón Sobrenatural",
            description = "A nivel 1 has acordado un trato con un ser sobrenatural de tu elección: el Señor Feérico, el Infernal o el Primigenio. Todos ellos están detallados al final de la descripción de esta clase. Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 1, 6, 10 y 14."
        ), Level(
            levelId = 22,
            classId = 3,
            level = 1,
            featureName = "Magia del Pacto",
            description = "Tus investigaciones en lo arcano y la magia otorgada por tu patrón te han brindado habilidades con los conjuros. Aquí está un resumen de cómo funcionan tus conjuros como brujo: Trucos: Comienzas con el conocimiento de dos trucos de tu elección de la lista de conjuros de brujo. A medida que subas de nivel, podrás aprender más trucos, como se indica en la columna \"trucos conocidos\" de la tabla del brujo. Espacios de Conjuro: La tabla del brujo muestra cuántos espacios de conjuro dispones y de qué nivel son esos espacios. Todos tus espacios son del mismo nivel. Para lanzar un conjuro de brujo de nivel 1 o superior, debes gastar un espacio de conjuro del nivel correspondiente. Recuperas todos los espacios utilizados tras finalizar un descanso corto o largo. Conjuros Conocidos de Nivel 1 y Superiores: Comienzas con el conocimiento de dos conjuros de nivel 1 de tu elección de la lista de conjuros de brujo. A medida que avances de nivel, podrás aprender más conjuros de brujo de nivel 1 o superior, siempre y cuando el nivel del conjuro sea igual o inferior al que aparece en la columna \"nivel de los espacios\" para tu nivel de brujo. Además, cada vez que subas de nivel, podrás reemplazar uno de tus conjuros conocidos por otro de la lista de conjuros de brujo. Aptitud Mágica: Tu Carisma es tu aptitud mágica para los conjuros de brujo. Utilizas tu Carisma para determinar la CD de las tiradas de salvación y las tiradas de ataque de tus conjuros de brujo. CD de tirada de salvación de conjuros = 8 + tu bonificador por competencia + tu modificador por Carisma  Modificador de ataque de conjuros = tu bonificador por competencia + tu modificador por Carisma  Canalizador Mágico: Puedes utilizar un canalizador arcano como canalizador mágico para los conjuros de brujo. Este canalizador arcano se describe en el capítulo de equipo."
        ), Level(
            levelId = 23,
            classId = 3,
            level = 2,
            featureName = "Invocaciones Sobrenaturales",
            description = "Durante tu estudio de lo oculto, has desenterrado invocaciones sobrenaturales: fragmentos de conocimiento prohibido que te imbuyen de una capacidad mágica perpetua. A nivel 2, obtienes dos invocaciones sobrenaturales a tu elección. Las invocaciones entre las que puedes elegir están detalladas al final de la descripción de esta clase. Podrás escoger más invocaciones sobrenaturales a medida que subas de nivel como brujo, según se indica en la columna \"invocaciones conocidas\" de la tabla del brujo. Además, cada vez que subas de nivel en esta clase, podrás elegir una de las invocaciones que ya conoces y reemplazarla por otra que puedas aprender a tu nuevo nivel."
        ), Level(
            levelId = 24,
            classId = 3,
            level = 3,
            featureName = "Beneficio del Pacto",
            description = "A nivel 3, tu patrón sobrenatural te concede un regalo como recompensa por tus leales servicios. Puedes elegir uno de los siguientes rasgos: Pacto de la Cadena: Aprendes el conjuro encontrar familiar y puedes lanzarlo como ritual. Este conjuro no cuenta dentro de tu límite de conjuros conocidos. Cuando lo lances, puedes elegir entre las formas habituales para un familiar o una de las siguientes formas especiales: diablillo, duende, pseudodragón o quasit. Además, cuando realizas la acción de Atacar, puedes renunciar a uno de tus ataques para permitir a tu familiar realizar uno de sus propios ataques utilizando su reacción. Pacto del Filo: Puedes usar tu acción para crear un arma de pacto en tu mano vacía. Puedes elegir qué forma adopta esta arma cuerpo a cuerpo cada vez que la crees (consulta el capítulo 5 para ver las armas disponibles). Mientras la empuñes, serás competente con ella. Esta arma cuenta como mágica a efectos de superar las resistencias e inmunidades a ataques y daño no mágicos. Tu arma de pacto desaparecerá si se encuentra a más de 5 pies de distancia de ti durante al menos 1 minuto, si haces desaparecer el arma voluntariamente o si mueres. También desaparecerá si usas este rasgo otra vez. Puedes transformar un arma mágica en tu arma de pacto llevando a cabo un ritual especial mientras la empuñas. Pacto del Grimorio: Tu patrón te entrega un grimorio llamado Libro de las Sombras. Cuando obtengas este rasgo, elige tres trucos de la lista de conjuros de cualquier clase. No tienen por qué ser los tres de la misma lista. Mientras tengas el libro contigo, puedes lanzar esos trucos a voluntad y no cuentan dentro de tu límite de trucos conocidos. Además, aunque no aparezcan en la lista de conjuros de brujo, para ti se considerarán como conjuros de brujo. Si pierdes el Libro de las Sombras, puedes llevar a cabo una ceremonia de 1 hora a través de la cual tu patrón te proporcionará un reemplazo. Esta ceremonia destruye el libro anterior, y el Libro de las Sombras se convierte en cenizas cuando mueres."
        ), Level(
            levelId = 25,
            classId = 4,
            level = 1,
            featureName = "Lanzamiento de Conjuros",
            description = "Como canalizador de poder divino, puedes lanzar conjuros de clérigo. Aquí está un resumen de cómo funcionan tus conjuros como clérigo: Trucos: A nivel 1, conoces tres trucos de tu elección escogidos de entre los de la lista de conjuros de clérigo. A medida que subas de nivel, podrás elegir más trucos de clérigo, como se indica en la columna \"trucos conocidos\" de la tabla del clérigo. Preparar y Lanzar Conjuros: La tabla del clérigo muestra cuántos espacios de conjuro dispones para lanzar conjuros de nivel 1 y superiores. Para lanzar uno de estos conjuros, debes invertir un espacio de al menos el nivel del conjuro. Puedes preparar una serie de conjuros, que son los que podrás lanzar, de entre la lista de conjuros de clérigo. La cantidad de conjuros que puedes preparar depende de tu nivel de clérigo y tu modificador por Sabiduría. Aptitud Mágica: La Sabiduría es tu aptitud mágica en lo que a conjuros de clérigo respecta. Utilizas tu Sabiduría para determinar la CD de las tiradas de salvación y las tiradas de ataque de tus conjuros de clérigo. CD de tirada de salvación de conjuros = 8 + tu bonificador por competencia + tu modificador por Sabiduría  Modificador de ataque de conjuros = tu bonificador por competencia + tu modificador por Sabiduría  Lanzamiento Ritual: Puedes lanzar de forma ritual aquellos conjuros de clérigo que tengas preparados y estén marcados como \"ritual\". Canalizador Mágico: Puedes utilizar un símbolo sagrado como canalizador mágico para los conjuros de clérigo. Este símbolo sagrado se describe en el capítulo de equipo."
        ), Level(
            levelId = 26,
            classId = 4,
            level = 1,
            featureName = "Dominio Divino",
            description = "Escoge uno de los siguientes dominios, que debe estar relacionado con tu dios: Conocimiento, Engaño, Guerra, Luz, Naturaleza, Tempestad o Vida. Estos dominios están detallados al final de la descripción de esta clase y cada uno de ellos incluye ejemplos de dioses asociados al mismo. Tu elección determinará qué conjuros de dominio y otros rasgos recibes a nivel 1. Además, también te proporcionará formas adicionales de usar Canalizar Divinidad, un rasgo que obtendrás a nivel 2. Por último, te otorgará ventajas específicas a los niveles 6, 8 y 17. CONJUROS DE DOMINIO: Cada dominio tiene su propia lista de conjuros, llamados conjuros de dominio, que conseguirás cuando alcances el nivel de clérigo indicado en la descripción de dicho dominio. Una vez obtenidos, estos conjuros siempre se considerarán preparados y no cuentan para el total de conjuros que puedes preparar cada día. Aunque recibas un conjuro de dominio que no aparezca en la lista de los de clérigo, para ti sí que se considerará como conjuro de clérigo."
        ), Level(
            levelId = 27,
            classId = 4,
            level = 2,
            featureName = "Canalizar Divinidad",
            description = "  A nivel 2 ganas la habilidad para canalizar energía divina directamente desde tu deidad y usar dicha energía para alimentar varios efectos mágicos. Empiezas con dos de estos efectos: Expulsar Muertos Vivientes y un segundo poder determinado por tu dominio. Algunos dominios te otorgan efectos adicionales al subir de nivel, tal y como se indica en sus descripciones. Cuando utilices Canalizar Divinidad, elige cuál de los efectos vas a crear. Deberás terminar un descanso corto o largo para poder volver a usar Canalizar Divinidad otra vez. Algunos efectos de Canalizar Divinidad exigen hacer tiradas de salvación. Cuando utilices uno de los proporcionados por esta clase, la CD será la misma que la de las tiradas de salvación de tus conjuros de clérigo. A partir de nivel 6, puedes emplear Canalizar Divinidad dos veces entre descansos y, a partir de nivel 18, tres veces. Recuperas todos los usos tras realizar un descanso corto o largo. CANALIZAR DIVINIDAD: EXPULSAR MUERTOS VIVIENTES Puedes utilizar tu acción para mostrar tu símbolo sagrado y rezar una oración que condene a los muertos vivientes. Todos los muertos vivientes que puedan verte u oírte a 30 pies o menos de ti deben realizar una tirada de salvación de Sabiduría. Si el objetivo falla su tirada de salvación, estará expulsado durante 1 minuto o hasta recibir daño. Una criatura expulsada deberá dedicar su turno a moverse lo más lejos posible de ti, si es que puede, y no podrá acercarse a ningún espacio a 30 pies o menos de ti. Además, tampoco será capaz de llevar a cabo reacciones. Solo puede realizar la acción de Correr o intentar escapar de un efecto que le impida moverse. Si no tiene a dónde moverse, llevará a cabo la acción de Esquivar."
        ), Level(
            levelId = 28,
            classId = 4,
            level = 2,
            featureName = "rasgo de Dominio Divino",
            description = ""
        ), Level(
            levelId = 30,
            classId = 5,
            level = 1,
            featureName = "Druidico",
            description = "Conoces el druídico, el idioma secreto de los druidas. Puedes hablarlo y utilizarlo para dejar mensajes ocultos. Tanto tú como cualquiera que conozca este idioma advertiréis inmediatamente de la presencia de estos mensajes. Los demás deberán superar una prueba de Sabiduría (Percepción) CD 15 para detectarlos, pero no podrán descifrarlos sin recurrir a la magia."
        ), Level(
            levelId = 31,
            classId = 5,
            level = 1,
            featureName = "Lanzamiento de conjuros",
            description = "Como druida, tu conexión con la esencia divina de la naturaleza te permite lanzar conjuros, dando forma a esta esencia con tu voluntad. Aquí está cómo funciona: Trucos  Nivel 1: Comienzas con el conocimiento de dos trucos de tu elección de la lista de conjuros de druida. A medida que subas de nivel, podrás aprender más trucos.  Preparar y Lanzar Conjuros  Espacios de Conjuro: Consulta la tabla del druida para saber cuántos espacios de conjuro tienes para lanzar conjuros de nivel 1 y superiores.  Preparación de Conjuros: Puedes preparar una serie de conjuros de entre la lista de conjuros de druida igual a tu nivel de druida más tu modificador por Sabiduría. Estos conjuros deben ser de un nivel para el que tengas espacios disponibles.  Cambiar Preparación: Puedes cambiar qué conjuros tienes preparados después de un descanso largo, pero requiere tiempo de estudio y meditación.  Aptitud Mágica  Sabiduría: Tu aptitud mágica en conjuros de druida se basa en tu Sabiduría, que proviene de tu conexión y sintonía con la naturaleza. Tu modificador por Sabiduría también determina la CD de las tiradas de salvación y las tiradas de ataque de tus conjuros de druida.  Lanzamiento Ritual  Puedes lanzar de forma ritual aquellos conjuros de druida que tengas preparados y estén marcados como \"ritual\".  Canalizador Mágico  Puedes utilizar un canalizador druídico como canalizador mágico para los conjuros de druida.  Esta es la base de tu práctica mágica como druida, aprovechando la esencia misma de la naturaleza para tus poderes."
        ), Level(
            levelId = 32,
            classId = 5,
            level = 2,
            featureName = "Forma Salvaje",
            description = "A partir de nivel 2, como druida, adquieres la habilidad de transformarte en una bestia que hayas visto antes mediante la magia. Aquí tienes cómo funciona este rasgo: Usar la Transformación: Puedes usar tu acción para adoptar la forma de una bestia que hayas visto antes. Tienes dos usos de este rasgo, y los recuperas después de un descanso corto o largo.  Formas de Bestia Disponibles: Tu nivel de druida determina las bestias en las que puedes transformarte, según lo indica la tabla de \"Formas de Bestia\". Por ejemplo, a nivel 2, puedes adoptar la forma de cualquier bestia con un valor de desafío de 1/4 o menos y que no tenga velocidades de natación o vuelo.  Duración de la Transformación: Puedes permanecer en forma de bestia durante un número de horas igual a la mitad de tu nivel de druida, redondeando hacia abajo. Después de este tiempo, vuelves a tu forma normal, a menos que gastes otro uso de este rasgo. También puedes volver a tu forma normal antes de tiempo usando una acción adicional durante tu turno.  Reglas mientras estás Transformado:  Tu perfil se reemplaza por el de la criatura, excepto tu alineamiento, personalidad y puntuaciones de Inteligencia, Sabiduría y Carisma. Mantienes tus competencias y habilidades, y ganas las competencias y acciones de la criatura.  Mantienes tus puntos de golpe y Dados de Golpe de la nueva forma, y cuando vuelves a tu forma normal, recuperas tus puntos de golpe anteriores. Sin embargo, si vuelves a tu forma normal al quedarte sin puntos de golpe, el exceso de daño se aplica a tu forma normal.  No puedes lanzar conjuros mientras estás transformado, y tu capacidad para hablar o realizar acciones que requieran el uso de las manos está limitada por la forma de la bestia.  Mantienes los beneficios de los rasgos de tu clase, raza u otras fuentes, siempre que tu nueva forma sea físicamente capaz de usarlos.  Equipo durante la Transformación: Decides si tu equipo cae al suelo en tu espacio, se funde con la nueva forma o lo sigues llevando puesto. El equipo que portes funciona normalmente, pero el DM determina si es factible que la nueva forma pueda llevarlo, y cualquier equipo que no pueda llevarse debe caer al suelo o fundirse con la nueva forma."
        ), Level(
            levelId = 33,
            classId = 5,
            level = 2,
            featureName = "Circulo druidico",
            description = "A nivel 2 eliges identificarte con un círculo druídico concreto: el Círculo de la Tierra o el Círculo de la Luna. Ambos están explicados al final de la descripción de esta clase. Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 2, 6, 10 y 14."
        ), Level(
            levelId = 40,
            classId = 6,
            level = 1,
            featureName = "Enemigo predilecto",
            description = "Desde el nivel 1, como un rastreador experimentado, tienes la capacidad de elegir un tipo de enemigo predilecto. Aquí tienes cómo funciona este rasgo: Tipo de Enemigo Predilecto: Puedes elegir entre aberraciones, autómatas, bestias, celestiales, cienos, dragones, elementales, feéricos, gigantes, infernales, monstruosidades, muertos vivientes o plantas. Alternativamente, puedes elegir dos razas de humanoides, como gnolls y orcos.  Ventajas en Pruebas y Conocimiento: Tienes ventaja en las pruebas de Sabiduría (Supervivencia) para rastrear a tus enemigos predilectos, así como en las pruebas de Inteligencia para recordar información sobre ellos.  Idioma Adicional: Aprendes un idioma de tu elección que sea hablado por tus enemigos predilectos, si es que poseen alguno.  Elección Adicional: A nivel 6 y 14, puedes elegir un enemigo predilecto adicional, junto con el idioma asociado a ese enemigo. Estas elecciones deberían reflejar los tipos de monstruos con los que te has encontrado durante tus aventuras."
        ), Level(
            levelId = 41,
            classId = 6,
            level = 1,
            featureName = "Explorador Nato",
            description = "Desde el nivel 1, como un explorador experto, tienes la capacidad de elegir un tipo de terreno predilecto. Aquí tienes cómo funciona este rasgo: Tipo de Terreno Predilecto: Puedes elegir entre bosque, costa, desierto, montaña, pantano, pradera, polar o Underdark.  Ventajas en Pruebas de Inteligencia y Sabiduría: Cuando hagas una prueba de Inteligencia o Sabiduría relacionada con tu terreno predilecto, añadirás dos veces tu bonificador por competencia si estás usando una habilidad en la que eres competente.  Beneficios al Viajar en tu Terreno Favorito:  El terreno difícil no ralentiza el viaje de tu grupo.  Tu grupo no puede perderse por causas no mágicas.  Permaneces atento al peligro incluso mientras realizas otras actividades durante el viaje.  Si viajas en solitario, puedes moverte con sigilo a un ritmo de viaje normal.  Cuando forrajeas, encuentras el doble de comida de lo normal.  Cuando rastreas a otras criaturas, también descubres el número exacto de las mismas, sus tamaños y hace cuánto pasaron por la zona.  Elección Adicional: A nivel 6 y 10, puedes elegir un terreno predilecto adicional. Estas elecciones deberían reflejar los entornos naturales en los que has desarrollado tu experiencia y habilidades."
        ), Level(
            levelId = 42,
            classId = 6,
            level = 2,
            featureName = "Estilo de Combate",
            description = "  A nivel 2, puedes elegir un estilo de combate como tu especialidad. Aquí están las opciones disponibles: Combate con Dos Armas: Cuando estés combatiendo con dos armas, puedes añadir tu modificador por característica al daño del segundo ataque. Defensa: Recibes un bono de +1 a la Clase de Armadura (CA) cuando lleves puesta cualquier armadura. Duelo: Cuando empuñes una única arma cuerpo a cuerpo que solo requiera de una mano para usarse, recibes un bono de +2 a tus tiradas de daño con esa arma. Tiro con Arco: Recibes un bonificador de +2 a las tiradas de ataque con armas a distancia. Por favor, elige la opción que prefieras."
        ), Level(
            levelId = 43,
            classId = 6,
            level = 2,
            featureName = "Lanzamiento de Conjuros",
            description = "A nivel 2, has aprendido a emplear la esencia mágica de la naturaleza, lanzando conjuros como lo haría un explorador. Aquí tienes la información relevante: Espacios de Conjuro  La tabla del explorador muestra de cuántos espacios de conjuro dispones para lanzar conjuros de nivel 1 y superiores. Para lanzar un conjuro, debes invertir un espacio de al menos el nivel del conjuro. Recuperas todos los espacios utilizados tras finalizar un descanso largo. Por ejemplo, si conoces el conjuro \"Encantar Animal\" de nivel 1 y posees un espacio de conjuro de nivel 1 y otro de nivel 2, podrías lanzar \"Encantar Animal\" empleando cualquiera de los dos espacios. Conjuros Conocidos de Nivel 1 y Superiores  A nivel 2, conoces dos conjuros de nivel 1 de tu elección, escogidos de entre la lista de conjuros de explorador. La tabla del explorador te indica cuándo podrás aprender más conjuros de explorador y cuántos elegir. Además, cada vez que subas de nivel en esta clase, podrás reemplazar uno de los conjuros de explorador que ya conoces por otro de la lista, siempre y cuando sea de un nivel para el que poseas espacios de conjuro. Aptitud Mágica  La Sabiduría es tu aptitud mágica en lo que respecta a los conjuros de explorador, ya que tu magia proviene de tu sintonía con la naturaleza. Utilizarás tu Sabiduría siempre que un conjuro de explorador haga referencia a tu aptitud mágica. Además, también usarás tu modificador por Sabiduría para determinar la CD de las tiradas de salvación y las tiradas de ataque de los conjuros de explorador que lances. CD tirada de salvación de conjuros = 8 + tu bonificador por competencia + tu modificador por Sabiduría  Modificador de ataque de conjuros = tu bonificador por competencia + tu modificador por Sabiduría  Si tienes alguna pregunta específica sobre los conjuros o cualquier otro aspecto de la clase de explorador, no dudes en preguntar."
        ), Level(
            levelId = 44,
            classId = 6,
            level = 3,
            featureName = "Arquetipo de Explorador",
            description = "A nivel 3, como explorador, tienes la opción de elegir un arquetipo al que aspiras emular. Puedes elegir entre dos opciones: Cazador o Señor de las Bestias. Ambos arquetipos te proporcionarán rasgos específicos a medida que avances en los niveles 3, 7, 11 y 15 de tu aventura. ¿Te gustaría más información sobre cada uno de los arquetipos para tomar tu decisión?"
        ), Level(
            levelId = 45,
            classId = 6,
            level = 3,
            featureName = "Percepción Primigenia",
            description = "A partir de nivel 3, puedes usar tu acción y gastar uno de tus espacios de conjuro de explorador para concentrar tu percepción en la región en la que te encuentras. Durante 1 minuto por cada nivel del espacio de conjuro que hayas invertido, puedes sentir si los siguientes tipos de criatura están presentes, como mucho, a 1 milla de distancia de ti: aberraciones, celestiales, dragones, elementales, feéricos, infernales y muertos vivientes. Esta percepción se extiende a 6 millas si estás en tu terreno predilecto. Este rasgo no revela ni el número de criaturas ni su ubicación."
        ), Level(
            levelId = 46,
            classId = 7,
            level = 1,
            featureName = "Estilo de combate",
            description = "Combate con armas a dos manos: Si sacas un 1 o un 2 en alguno de los dados de daño de un ataque hecho con un arma cuerpo a cuerpo que empuñes con las dos manos, puedes volver a tirar el dado en cuestión. Debes usar el nuevo resultado, incluso si es un 1 o un 2. Para poder obtener este beneficio, el arma debe poseer la propiedad 'versátil' o 'a dos manos'. Combate con dos armas: Cuando estés combatiendo con dos armas, puedes añadir tu modificador por característica al daño del segundo ataque. Defensa: Recibes un +1 a la Clase de Armadura (CA) cuando lleves puesta cualquier armadura. Duelo: Cuando empuñes una única arma cuerpo a cuerpo que solo requiera de una mano para usarse, recibes un +2 a tus tiradas de daño con esa arma. Protección: Cuando una criatura que puedas ver ataque a un objetivo que esté a 5 pies o menos de ti y no seas tú mismo, puedes utilizar tu reacción para dar desventaja a la tirada de ataque. Debes estar empuñando un escudo. Tiro con arco: Recibes un bonificador de +2 a las tiradas de ataque con armas a distancia."
        ), Level(
            levelId = 47,
            classId = 7,
            level = 1,
            featureName = "Tomar aliento",
            description = "Reserva de Energía: Posees una pequeña reserva de energías, a la que puedes recurrir para protegerte del peligro. Puedes usar una acción adicional durante tu turno para recuperar tantos puntos de golpe como 1d10 + tu nivel de guerrero. Una vez utilizado este rasgo, deberás terminar un descanso corto o largo para poder volver a emplearlo de nuevo."
        ), Level(
            levelId = 48,
            classId = 7,
            level = 2,
            featureName = "Acción subita",
            description = "Acción Completa: A partir de nivel 2, puedes superar tus propios límites durante un instante. Durante tu turno, puedes llevar a cabo una acción más, además de tu acción y acción adicional habituales. Una vez utilizado este rasgo, deberás terminar un descanso corto o largo para poder volver a emplearlo de nuevo. A partir de nivel 17, puedes usar este rasgo dos veces antes de descansar, pero solo una vez por turno."
        ), Level(
            levelId = 49,
            classId = 7,
            level = 3,
            featureName = "Arquetipo marcial",
            description = "Arquetipo de Combate: A nivel 3, escoges un arquetipo al que aspiras emular con tu estilo y técnicas de combate. Elige entre Campeón, Maestro del Combate o Caballero Arcano. Todos ellos están detallados al final de la descripción de esta clase. Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 3, 7, 10, 15 y 18."
        ), Level(
            levelId = 50,
            classId = 8,
            level = 1,
            featureName = "Lanzamiento de Conjuros",
            description = "Origen de la Magia Arcana: Un evento de tu pasado, o de la vida de uno de tus padres o ancestros, dejó una marca indeleble en ti, llenándote de magia arcana. Esta fuente de magia, sea cual sea su origen, alimenta tus conjuros. El capítulo 10 contiene las reglas de lanzamiento de conjuros y el 11 la lista de conjuros de hechicero. Trucos: A nivel 1 conoces cuatro trucos de tu elección escogidos de entre los de la lista de conjuros de hechicero. Podrás elegir más trucos de hechicero cuando llegues a niveles más altos, como se indica en la columna \"trucos conocidos\" de la tabla del hechicero. Espacios de Conjuro: La tabla del hechicero muestra de cuántos espacios de conjuro dispones para lanzar conjuros de nivel 1 y superiores. Para lanzar uno de estos conjuros deberás invertir un espacio de al menos el nivel del conjuro. Recuperas todos los espacios utilizados tras finalizar un descanso largo. Conjuros Conocidos de Nivel 1 y Superiores: Conoces dos conjuros de nivel 1 de tu elección, escogidos de entre la lista de conjuros de hechicero. La columna \"conjuros conocidos\" de la tabla del hechicero te indica cuándo podrás aprender más conjuros de hechicero y cuántos elegir. Aptitud Mágica: El Carisma es tu aptitud mágica en lo que a conjuros de hechicero respecta, ya que el poder de tu magia descansa en tu habilidad para proyectar tu voluntad sobre el mundo. Así, utilizarás tu Carisma siempre que un conjuro de hechicero haga referencia a tu aptitud mágica. Canalizador Mágico: Puedes utilizar un canalizador arcano (mira el capítulo 5: \"Equipo\") como canalizador mágico para los conjuros de hechicero."
        ), Level(
            levelId = 51,
            classId = 8,
            level = 1,
            featureName = "Origen Mágico",
            description = "Origen Mágico: Elige un origen mágico, que describe la procedencia de tus poderes innatos. Puedes escoger entre Linaje Dracónico o Magia Salvaje. Ambos están detallados al final de la descripción de esta clase. Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 1, 6, 14 y 18."
        ), Level(
            levelId = 52,
            classId = 8,
            level = 2,
            featureName = "Fuente de Magia",
            description = "PUNTOS DE HECHICERÍA: Posees 2 puntos de hechicería, pero obtendrás más según subas de nivel en esta clase, tal y como se muestra en la columna \"Puntos de Hechicería\" de la tabla del hechicero. Nunca puedes tener más puntos de hechicería de los que aparecen en dicha columna para tu nivel. Recuperas todos los puntos de hechicería utilizados tras finalizar un descanso largo. LANZAMIENTO FLEXIBLE: Crear Espacios de Conjuro: Puedes usar tus puntos de hechicería para obtener espacios de conjuro adicionales y viceversa: sacrificar espacios de conjuro para conseguir puntos de hechicería adicionales. Aprenderás otras formas de utilizar tus puntos de hechicería cuando alcances niveles superiores.  Crear Espacios de Conjuro: Puedes emplear una acción adicional durante tu turno para transformar puntos de hechicería que no hayas gastado aún en un espacio de conjuro. La tabla \"Crear Espacios de Conjuro\" indica el coste de crear un espacio de conjuro de un nivel determinado. No puedes crear espacios de conjuro de niveles superiores a 5. Cualquier espacio creado mediante este rasgo se desvanece cuando finalizas un descanso largo.  Convertir un Espacio de Conjuro en Puntos de Hechicería: Puedes utilizar una acción adicional durante tu turno para gastar un espacio de conjuro y obtener tantos puntos de hechicería como el nivel del espacio."
        ), Level(
            levelId = 53,
            classId = 8,
            level = 3,
            featureName = "Metamagia",
            description = "CONJURO ACELERADO: Cuando lanzas un conjuro con un tiempo de lanzamiento de 1 acción, puedes gastar 2 puntos de hechicería para hacer que el tiempo de lanzamiento sea solo de 1 acción adicional a efectos de este lanzamiento.  CONJURO CUIDADOSO: Cuando lanzas un conjuro que obliga a otras criaturas a realizar una tirada de salvación, puedes proteger a algunas de esas criaturas de la fuerza completa del conjuro. Para hacer esto, debes gastar 1 punto de hechicería y escoger, como mucho, tantas criaturas como tu modificador por Carisma (como mínimo una criatura). Las criaturas elegidas tendrán éxito en sus tiradas de salvación contra el conjuro automáticamente.  CONJURO DISTANTE: Cuando lanzas un conjuro que tenga un alcance de, como mínimo, 5 pies, puedes gastar 1 punto de hechicería para duplicar el alcance del conjuro. Cuando lanzas un conjuro que posea un alcance de toque, puedes gastar 1 punto de hechicería para hacer que su alcance sea de 30 pies.  CONJURO EXTENDIDO: Cuando lanzas un conjuro que tenga una duración de al menos 1 minuto, puedes gastar 1 punto de hechicería para duplicar su duración, hasta un máximo de 24 horas.  CONJURO GEMELO: Cuando lanzas un conjuro que tiene como objetivo a una sola criatura y no posee un alcance de lanzador, puedes gastar tantos puntos de hechicería como el nivel del conjuro para elegir también como objetivo del mismo conjuro a una segunda criatura dentro del alcance (1 punto de hechicería si el conjuro es un truco). Para que un conjuro pueda beneficiarse de esta opción, este debe ser incapaz de escoger más de un objetivo al nivel al que lo lanzas. Así, proyectil mágico y rayo abrasador no podrían verse afectados por Conjuro Gemelo, pero rayo de escarcha y orbe cromático sí.  CONJURO INTENSIFICADO: Cuando lanzas un conjuro que obliga a al menos una criatura a hacer una tirada de salvación para resistir sus efectos, puedes gastar 3 puntos de hechicería para conseguir que uno de los objetivos del conjuro tenga desventaja en la primera tirada de salvación que realice contra el conjuro.  CONJURO POTENCIADO: Cuando tiras el daño de un conjuro, puedes gastar 1 punto de hechicería para repetir la tirada de, como mucho, tantos dados de daño como tu modificador por Carisma (como mínimo un dado). Eso sí, deberás usar los nuevos resultados. Puedes emplear Conjuro Potenciado incluso si ya has utilizado otra opción de Metamagia durante el lanzamiento del conjuro.  CONJURO SUTIL: Cuando lanzas un conjuro, puedes gastar 1 punto de hechicería para realizarlo sin tener que utilizar componentes somáticos ni verbales."
        ), Level(
            levelId = 54,
            classId = 9,
            level = 1,
            featureName = "Lanzamiento de conjuros",
            description = "LIBRO DE CONJUROS: A nivel 1 posees un libro de conjuros que contiene seis conjuros de mago de nivel 1 de tu elección. Este libro es el depositario de los conjuros de mago que conoces, con la excepción de los trucos, que están grabados en tu mente.  PREPARAR Y LANZAR CONJUROS: La tabla del mago muestra de cuántos espacios de conjuro dispones para lanzar conjuros de nivel 1 y superiores. Para lanzar uno de estos conjuros deberás invertir un espacio de al menos el nivel del conjuro. Recuperas todos los espacios utilizados tras finalizar un descanso largo.  Puedes preparar una serie de conjuros de mago, que son los que podrás lanzar. Para hacer esto, escoge tantos conjuros de mago como tu nivel de mago + tu modificador por Inteligencia (como mínimo un conjuro). Todos estos conjuros deben estar escritos en tu libro de conjuros y ser de un nivel para el que tengas espacios.  Si eres un mago de nivel 3, tendrás cuatro espacios de nivel 1 y dos espacios de nivel 2. Con una Inteligencia de 16, podrías preparar cualquier combinación de seis conjuros de nivel 1 o 2, siempre y cuando todos ellos figuren en tu libro de conjuros. Además, si prepararas un conjuro de nivel 1, como proyectil mágico, podrías lanzarlo usando un espacio tanto de nivel 1 como de nivel 2. Lanzar un conjuro no hace que desaparezca de tu lista de conjuros preparados.  Puedes cambiar qué conjuros tienes preparados tras finalizar un descanso largo. Para preparar una nueva serie de conjuros es necesario pasar cierto tiempo estudiando tu libro de conjuros y memorizando los ensalmos y gestos que debes ejecutar para poder lanzarlos: al menos 1 minuto por cada nivel de cada conjuro que prepares.  APTITUD MÁGICA: La Inteligencia es tu aptitud mágica en lo que a conjuros de mago respecta, ya que aprendes tus conjuros mediante el estudio concienzudo y la memorización. Así, utilizarás tu Inteligencia siempre que un conjuro de mago haga referencia a tu aptitud mágica. Además, también usarás tu modificador por Inteligencia para determinar la CD de las tiradas de salvación y las tiradas de ataque de los conjuros de mago que lances.  CD tirada de salvación de conjuros = 8 + tu bonificador por competencia + tu modificador por Inteligencia  Modificador de ataque de conjuros = tu bonificador por competencia + tu modificador por Inteligencia"
        ), Level(
            levelId = 55,
            classId = 9,
            level = 1,
            featureName = "Recuperación arcana",
            description = "Has aprendido a recuperar parte de tus energías mágicas estudiando tu libro de conjuros. Una vez al día, tras finalizar un descanso corto, puedes elegir espacios de conjuro gastados y recuperarlos. La suma de niveles de estos espacios de conjuro debe ser igual o inferior a la mitad de tu nivel de mago (redondeando hacia arriba), y ninguno de los espacios puede ser de nivel 6 o más. Por ejemplo, si eres un mago de nivel 4, podrás recuperar hasta dos niveles en espacios de conjuro: un espacio de nivel 2 o dos de nivel 1."
        ), Level(
            levelId = 56,
            classId = 9,
            level = 2,
            featureName = "Tradición arcana",
            description = "Cuando llegas a nivel 2, escoges una tradición arcana, que moldea tu práctica de la magia a través de la óptica de una de las ocho escuelas: Abjuración, Adivinación, Conjuración, Encantamiento, Evocación, Ilusionismo, Nigromancia y Transmutación. Todas ellas están detalladas al final de la descripción de esta clase. Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 2, 6, 10 y 14."
        ), Level(
            levelId = 57,
            classId = 10,
            level = 1,
            featureName = "Artes marciales",
            description = "Práctica de las Artes Marciales:  A nivel 1, tu práctica de las artes marciales te ha otorgado un dominio de los estilos de combate que emplean ataques sin armas y armas de monje, que son las espadas cortas y cualquier arma sencilla que no posea las propiedades \"a dos manos\" o \"pesada\". Obtienes los siguientes beneficios mientras no portes armadura ni escudo, y estés desarmado o empuñes únicamente armas de monje: Puedes usar tu Destreza en lugar de tu Fuerza para las tiradas de ataque y de daño de tus ataques sin armas y tus armas de monje.  Puedes tirar 1d4 en lugar del daño normal de tus ataques sin armas o tus armas de monje. Este dado cambia según subes niveles como monje, tal y como se muestra en la columna \"artes marciales\" de la tabla del monje.  Cuando llevas a cabo la acción de Atacar con un ataque sin armas o un arma de monje durante tu turno, puedes utilizar tu acción adicional para hacer un ataque sin armas. De este modo, si empleas la acción de Atacar para atacar con un bastón, también puedes realizar un ataque sin armas con tu acción adicional, siempre y cuando no hayas usado ya esta. Ciertos monasterios utilizan armas de monje específicas. Por ejemplo, podrías usar un garrote llamado nunchaku, que está compuesto de dos trozos de madera unidos por una cadena corta, o una hoz con un filo más corto y recto, que recibe el nombre de kama. Independientemente del nombre que reciba el arma de monje, esta utilizará los valores de juego del arma de la que se deriva, tal y como se indica en el capítulo 5: \"Equipo\"."
        ), Level(
            levelId = 58,
            classId = 10,
            level = 1,
            featureName = "Defensa sin armadura",
            description = "A partir de nivel 1, si no estás portando armadura alguna ni embrazando un escudo, tu Clase de Armadura será 10 + tu modificador por Destreza+ tu modificador por Sabiduría."
        ), Level(
            levelId = 59,
            classId = 10,
            level = 2,
            featureName = "Ki",
            description = "Control de Ki:  A partir de nivel 2, tu entrenamiento te permite controlar la energía mística llamada \"ki\". Tu capacidad de emplearla se representa mediante una serie de puntos de ki. Tu nivel de monje determina cuántos de estos puntos posees, como se muestra en la columna \"puntos de ki\" de la tabla del monje. Puedes utilizar estos puntos para alimentar varios rasgos ki. Empiezas con tres de estos rasgos: Ráfaga de Golpes, Defensa Paciente y Paso del Viento. Aprenderás más rasgos ki según vayas subiendo de nivel en esta clase. Cuando uses un punto de ki, este dejará de estar disponible hasta que termines un descanso corto o largo, al final del cual volverás a reunir el ki en tu interior. Debes estar meditando durante al menos 30 minutos del descanso para poder recuperar los puntos de ki. Algunos de tus rasgos ki exigen a tu objetivo hacer una tirada de salvación para resistir sus efectos. La CD para estas tiradas de salvación se calcula de la siguiente forma: CD tirada de salvación de ki = 8 + tu bonificador por competencia + tu modificador por Sabiduría."
        ), Level(
            levelId = 60,
            classId = 10,
            level = 2,
            featureName = "Movimiento sin Armadura",
            description = "Movimiento Ágil:  A partir de nivel 2, si no estás llevando armadura ni escudo, tu velocidad aumenta en 10 pies. Esta bonificación aumenta según alcanzas ciertos niveles de monje, tal y como se muestra en la tabla del monje. Desplazamiento en Superficies Verticales y Líquidos:  A nivel 9 obtienes la capacidad para moverte, durante tu turno, por superficies verticales y sobre líquidos sin caerte."
        ), Level(
            levelId = 61,
            classId = 10,
            level = 3,
            featureName = "Tradición Monástica",
            description = "Elección de la Tradición Monástica:  Cuando alcanzas el nivel 3, te entregas a una tradición monástica de entre las siguientes: el Camino de la Mano Abierta, el Camino de la Sombra o el Camino de los Cuatro Elementos. Todas ellas están detalladas al final de la descripción de esta clase. Tu tradición te proporciona ciertos rasgos cuando alcanzas los niveles 3, 6, 11 y 17."
        ), Level(
            levelId = 62,
            classId = 10,
            level = 3,
            featureName = "Desviar Proyectiles",
            description = "Capacidad de Deflectar Proyectiles:  A partir de nivel 3, puedes usar tu reacción para desviar o atrapar el proyectil de un ataque con arma a distancia que te haya impactado. Si haces esto, el daño que recibes del ataque se reduce en 1d10 + tu modificador por Destreza + tu nivel de monje. Si disminuyes el daño a 0, podrás atrapar el proyectil, siempre y cuando este sea lo bastante pequeño como para que puedas sujetarlo con una mano y tengas al menos una mano libre. Si atrapas el proyectil, puedes gastar 1 punto de ki para, como parte de la misma reacción, realizar un ataque a distancia con el arma o unidad de munición que acabas de atrapar. Haces este ataque como si fueras competente, independientemente de tus competencias con armas, y el proyectil cuenta como un arma de monje a efectos del ataque, que tiene un alcance normal de 20 pies y un alcance largo de 60 pies."
        ), Level(
            levelId = 63,
            classId = 11,
            level = 1,
            featureName = "Sentidos divinos",
            description = "Sentido del Bien y del Mal:  La presencia de un fuerte mal se manifiesta en tus sentidos como un hedor nauseabundo, mientras que un poderoso bien resuena en tus oídos como música celestial. Como acción, puedes expandir tu percepción para detectar estas fuerzas. Hasta el final de tu siguiente turno, sabrás la ubicación de cualquier celestial, infernal o muerto viviente a 60 pies o menos de ti que no se encuentre tras cobertura completa. Conocerás el tipo (celestial, infernal o muerto viviente) de cualquier ser cuya presencia puedas percibir, pero no su identidad (el conde vampiro Strahd von Zarovich, por ejemplo). Dentro de esa misma distancia también podrás detectar la presencia de cualquier lugar u objeto que haya sido consagrado o profanado, como con el conjuro consagrar. Puedes emplear este rasgo tantas veces como 1 + tu modificador por Carisma. Recuperas todos los usos tras finalizar un descanso largo."
        ), Level(
            levelId = 64,
            classId = 11,
            level = 1,
            featureName = "Imponer las Manos",
            description = "Imponer las Manos:  Tu toque bendito puede sanar heridas. Posees una reserva de poder curativo que se recupera cuando llevas a cabo un descanso largo. Puedes recurrir a esta reserva para restaurar tantos puntos de golpe como cinco veces tu nivel de paladín. Como acción, puedes tocar a una criatura y tomar cierta cantidad de poder de tu reserva para hacer recuperar a la criatura los puntos de golpe que elijas, siempre que este número no supere la cantidad restante en tu reserva. Como alternativa, puedes gastar 5 puntos de golpe de tu reserva para curar al objetivo de una enfermedad o neutralizar un veneno que le esté afectando. Eres capaz de curar varias enfermedades y neutralizar varios venenos con un solo uso de Imponer las Manos, pero deberás gastar los puntos de golpe necesarios para todos ellos. Este rasgo no afecta a muertos vivientes ni a autómatas."
        ), Level(
            levelId = 65,
            classId = 11,
            level = 2,
            featureName = "Estilo de Combate",
            description = "Combate con Armas a Dos Manos: Si sacas un 1 o un 2 en alguno de los dados de daño de un ataque hecho con un arma cuerpo a cuerpo que empuñes con las dos manos, puedes volver a tirar el dado en cuestión. Debes usar el nuevo resultado, incluso aunque este sea un 1 o un 2. Para poder obtener este beneficio, el arma debe poseer la propiedad \"versátil\" o \"a dos manos\". Defensa: Recibes un +1 a la Clase de Armadura (CA) cuando lleves puesta cualquier armadura. Duelo: Cuando empuñes una única arma cuerpo a cuerpo que solo requiera de una mano para usarse, recibes un +2 a tus tiradas de daño con esa arma. Protección: Cuando una criatura que puedas ver ataque a un objetivo que esté a 5 pies o menos de ti y no seas tú mismo, puedes utilizar tu reacción para dar desventaja a la tirada de ataque. Debes estar empuñando un escudo."
        ), Level(
            levelId = 66,
            classId = 11,
            level = 2,
            featureName = "Lanzamiento de Conjuros",
            description = "Preparar y Lanzar Conjuros: La tabla del paladín muestra cuántos espacios de conjuro tienes. Para lanzar un conjuro de paladín de nivel 1 o superior, debes gastar un espacio de conjuro de al menos el mismo nivel que el del conjuro que deseas lanzar. Recuperas todos los espacios de conjuro utilizados después de un descanso largo.  Puedes preparar una serie de conjuros de la lista de conjuros de paladín. El número de conjuros que puedes preparar está determinado por tu modificador de Carisma más la mitad de tu nivel de paladín, redondeando hacia abajo. Puedes cambiar los conjuros preparados después de un descanso largo, lo que requiere tiempo de estudio y meditación.  Aptitud Mágica: Tu Carisma es tu aptitud mágica para los conjuros de paladín, ya que su poder proviene de la fuerza de tus convicciones. Utilizas tu Carisma para determinar la CD de las tiradas de salvación y las tiradas de ataque de los conjuros de paladín que lances.  Canalizador Mágico: Puedes utilizar un símbolo sagrado como canalizador mágico para tus conjuros de paladín."
        ), Level(
            levelId = 67,
            classId = 11,
            level = 2,
            featureName = "Castigo Divino",
            description = "A partir del nivel 2, cuando golpeas a una criatura con un ataque cuerpo a cuerpo, puedes gastar uno de tus espacios de conjuro para infligir daño radiante adicional al objetivo, además del daño causado por el arma. Este daño adicional es de 2d8 si gastas un espacio de nivel 1, más 1d8 adicional por cada nivel del espacio de conjuro por encima de 1, hasta un máximo de 5d8.  Este daño adicional aumenta en 1d8 si el objetivo es un muerto viviente o un infernal."
        ), Level(
            levelId = 68,
            classId = 11,
            level = 3,
            featureName = "Salud Divina",
            description = "A partir de nivel 3, la magia que fluye a través de ti te hace inmune a las enfermedades."
        ), Level(
            levelId = 69,
            classId = 11,
            level = 3,
            featureName = "Juramento Sagrado",
            description = "Cuando alcanzas el nivel 3, pronuncias el juramento que te ata para siempre a la senda del paladín. Hasta este momento has pasado simplemente por una fase de preparación, comprometido con tu deber, pero sin haber hecho aún el juramento. Ahora debes elegir entre el Juramento de Entrega, el Juramento de los Antiguos o el Juramento de Venganza. Todos ellos están explicados al final de la descripción de esta clase.  Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 3, 7, 15 y 20. Entre estos rasgos se encuentran los conjuros de juramento y Canalizar Divinidad.  Conjuros de Juramento: Cada juramento tiene una serie de conjuros asociados. Puedes acceder a estos conjuros cuando llegas a los niveles especificados en la descripción del juramento en cuestión. Una vez accedes a ellos, estos conjuros siempre se considerarán preparados y no cuentan para el total de conjuros que puedes preparar cada día.  Aunque obtengas un conjuro de juramento que no aparezca en la lista de los de paladín, para ti sí que se considerará como conjuro de paladín.  Canalizar Divinidad: Tu juramento te permite canalizar energía divina para alimentar varios efectos mágicos. Cada forma de utilizar Canalizar Divinidad que tu juramento te proporciona incluye una explicación de cómo usarla.  Cuando empleas Canalizar Divinidad, elige cuál de las opciones vas a emplear. Deberás terminar un descanso corto o largo para poder volver a usar Canalizar Divinidad otra vez.  Algunos efectos de Canalizar Divinidad exigen hacer tiradas de salvación. Cuando utilices uno de los proporcionados por esta clase, la CD será la misma que la de las tiradas de salvación de tus conjuros de paladín."
        ), Level(
            levelId = 70,
            classId = 12,
            level = 1,
            featureName = "Pericia",
            description = "Nivel 1: Escoge dos de tus competencias en habilidades, o bien solo una y tu competencia con las herramientas de ladrón. Tu bonificador por competencia se duplica para cualquier prueba de característica que hagas utilizando cualquiera de las dos competencias elegidas.  Nivel 6: Puedes elegir otras dos competencias (en habilidades o con herramientas de ladrón) y obtener este beneficio para ellas."
        ), Level(
            levelId = 71,
            classId = 12,
            level = 1,
            featureName = "Ataque Furtivo",
            description = "Una vez por turno, puedes infligir 1d6 de daño adicional a una criatura a la que impactes con un ataque en cuya tirada de ataque tuvieras ventaja. Este ataque debe haber sido hecho utilizando un arma sutil o a distancia.  No necesitas tener ventaja en la tirada de ataque si otro enemigo del objetivo está a 5 pies o menos de él, dicho enemigo no está incapacitado, y no sufres desventaja en la tirada de ataque.  La cantidad de daño adicional aumenta según subes de nivel en esta clase, tal y como se indica en la columna \"ataque furtivo\" de la tabla del pícaro."
        ), Level(
            levelId = 72,
            classId = 12,
            level = 1,
            featureName = "Jerga de Ladrones",
            description = "Aprendiste la jerga de ladrones, una mezcla de dialecto, argot y código secreto que te permite esconder mensajes en lo que parece una conversación normal y corriente. Solo aquellas criaturas que conozcan la jerga de ladrones podrán entender estos mensajes.  Expresar un mensaje utilizando esta jerga precisa de cuatro veces el tiempo que tardarías en comunicar la misma idea directamente.  Además, también comprendes un conjunto de señales y símbolos secretos que se usan para dejar mensajes cortos y sencillos, como el hecho de que una zona sea peligrosa o el territorio de un gremio de ladrones, si hay o no botín cerca, o si los lugareños de los alrededores son presas fáciles o pueden proporcionar un piso franco para ladrones a la fuga."
        ), Level(
            levelId = 73,
            classId = 12,
            level = 2,
            featureName = "Acción Astuta",
            description = "Tu agilidad mental y rapidez te permiten moverte y actuar con presteza, por lo que puedes llevar a cabo una acción adicional en cada uno de tus turnos durante un combate.  Solo puedes utilizar esta acción adicional para realizar las acciones de Correr, Destrabarse o Esconderse."
        ), Level(
            levelId = 74,
            classId = 12,
            level = 3,
            featureName = "Arquetipo de Pícaro",
            description = "Escoges un arquetipo al que aspiras emular, desarrollando tus habilidades como pícaro siguiendo ese modelo.  Elige entre Ladrón, Asesino o Embaucador Arcano. Todos ellos están detallados al final de la descripción de esta clase.  Esta elección te proporciona ciertos rasgos cuando alcanzas los niveles 3, 9, 13 y 17."
        )
    )

    for (levelObj in levels) {
        val values = ContentValues().apply {
            put("class_id", levelObj.classId)
            put("level", levelObj.level)
            put("feature_name", levelObj.featureName)
            put("description", levelObj.description)
        }
        db.insert("class_levels", null, values)
    }
}
