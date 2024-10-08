package nl.ndat.tvlauncher.data

import android.content.Context
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.SuspendingTransactionWithReturn
import app.cash.sqldelight.SuspendingTransactionWithoutReturn
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import nl.ndat.tvlauncher.data.sqldelight.Channel
import nl.ndat.tvlauncher.data.sqldelight.ChannelProgram
import nl.ndat.tvlauncher.data.sqldelight.Database
import nl.ndat.tvlauncher.data.sqldelight.Input

class DatabaseContainer(
	context: Context
) {
	companion object {
		const val DB_FILE = "data.db"
	}

	private val driver = AndroidSqliteDriver(Database.Schema.synchronous(), context, DB_FILE)
	private val database = Database(
		driver = driver,
		ChannelAdapter = Channel.Adapter(
			typeAdapter = EnumColumnAdapter(),
		),
		ChannelProgramAdapter = ChannelProgram.Adapter(
			weightAdapter = IntColumnAdapter(),
			typeAdapter = EnumColumnAdapter(),
			posterArtAspectRatioAdapter = EnumColumnAdapter(),
			lastPlaybackPositionMillisAdapter = IntColumnAdapter(),
			durationMillisAdapter = IntColumnAdapter(),
			itemCountAdapter = IntColumnAdapter(),
			interactionTypeAdapter = EnumColumnAdapter(),
		),
		InputAdapter = Input.Adapter(
			typeAdapter = EnumColumnAdapter()
		),
	)

	val apps = database.appQueries
	val inputs = database.inputQueries
	val channels = database.channelQueries
	val channelPrograms = database.channelProgramQueries

	suspend fun transaction(body: suspend SuspendingTransactionWithoutReturn.() -> Unit) = database.transaction { body() }
	suspend fun <T> transactionForResult(body: suspend SuspendingTransactionWithReturn<T>.() -> T) = database.transactionWithResult { body() }
}

class IntColumnAdapter : ColumnAdapter<Int, Long> {
	override fun decode(databaseValue: Long): Int = databaseValue.toInt()
	override fun encode(value: Int): Long = value.toLong()
}
