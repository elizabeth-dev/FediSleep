package sh.elizabeth.fedisleep.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import sh.elizabeth.fedisleep.InternalData
import sh.elizabeth.fedisleep.data.datasource.InternalDataSerializer
import sh.elizabeth.fedisleep.util.Dispatcher
import sh.elizabeth.fedisleep.util.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

	@Singleton
	@Provides
	fun provideInternalDataDataStore(
		@ApplicationContext context: Context,
		@Dispatcher(Dispatchers.IO) ioDispatcher: CoroutineDispatcher,
		@ApplicationScope scope: CoroutineScope,
		internalDataSerializer: InternalDataSerializer,
	): DataStore<InternalData> = DataStoreFactory.create(
		serializer = internalDataSerializer,
		scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
	) {
		context.dataStoreFile("internal.pb")
	}
}
