import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.abriljavier.milistadeheroes.DatabaseHelper

class InitialDataInsertHelper(private val context: Context) {

    fun insertInitialData() {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        insertRace(db, 2, "Draconido", "{\"STR\": 2,\"DEX\": 0,\"CON\": 0,\"INT\": 0,\"WIS\": 0,\"CHA\": 1}",
            "Mediano", 30, "Común y Dracónico",
            "{\"0\": \"Linaje Dracónico: Posees la sangre de dragones. Escoge de qué tipo en la tabla linaje dracónico. Tu Ataque de Aliento y Resistencia al Daño vendrán de terminadas por el tipo de dragón, tal y como se indica en dicha tabla.\",\"1\": \"Ataque de Aliento Puedes utilizar tu acción para exhalar energía destructora. Tu Linaje Dracónico determina el tamaño, forma y tipo de daño del aliento. Cuando uses tu Ataque de Aliento, las criaturas que se encuentren en la zona cubierta por este deberán hacer una tirada de salvación, cuyo tipo viene definido por tu linaje. La CD de esta tirada de salvación es de 8 + tu modificador por Constitución + tu bonificador por competencia. Las criaturas sufrirán 2d6 de daño si fallan la tirada o la mitad de ese daño si la superan. El daño aumenta a 3d6 cuando alcanzas el nivel 6, a 4d6 a nivel 11 y a 5d6 a nivel 16. Una vez utilizado el Ataque de Aliento, no podrás volver a emplearlo hasta que finalices un descanso corto o largo.\",\"3\": \"Resistencia al Daño. Posees resistencia al tipo de daño asociado a tu Linaje Dracónico.\"}")

        insertRace(db, 3, "Enano de las colinas", "{\"STR\": 0,\"DEX\": 0,\"CON\": 2,\"INT\": 0,\"WIS\": 1,\"CHA\": 0}",
            "Mediano", 25, "Común y Enano",
            "{\"0\": \"Visión en la Oscuridad. Acostumbrado a la vida bajo tierra, puedes ver bien en la oscuridad o con poca luz. Eres capaz de percibir hasta a 60 pies en luz tenue como si hubiera luz brillante, y esa misma distancia en la oscuridad como si hubiera luz tenue. Eso sí, no puedes distinguir colores en la oscuridad, solo tonos de gris.\",\"1\": \"Resistencia Enana. Tienes ventaja en las tiradas de salvación contra veneno y posees resistencia al daño de veneno (como se explica en el capítulo 9: 'Combate').\",\"2\": \"Entrenamiento de Combate Enano. Eres competente con hachas de guerra, hachas de mano, martillos de guerra y martillos ligeros.\",\"3\": \"Competencia con Herramientas. Eres competente con las herramientas de artesano que elijas de entre las siguientes: herramientas de albañil, herramientas de herrero o suministros de cervecero.\",\"4\": \"Afinidad con la Piedra. Cuando hagas una prueba de Inteligencia (Historia) que tenga relación con el origen de un trabajo en piedra, se te considerará competente en la habilidad Historia y añadirás dos veces tu bonificador por competencia a la tirada, en lugar de solo una.\"}")

        // Insertar más datos de razas...

        db.close()
    }

    private fun insertRace(
        db: SQLiteDatabase,
        raceId: Int,
        raceName: String,
        characteristicBoost: String,
        size: String,
        speed: Int,
        languages: String,
        attributes: String
    ) {
        val contentValues = ContentValues()
        contentValues.put("race_id", raceId)
        contentValues.put("race_name", raceName)
        contentValues.put("characteristic_boost", characteristicBoost)
        contentValues.put("size", size)
        contentValues.put("speed", speed)
        contentValues.put("languajes", languages)
        contentValues.put("attributes", attributes)

        db.insert("races", null, contentValues)
    }
}