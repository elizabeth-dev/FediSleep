package sh.elizabeth.fedisleep.data.datasource

import sh.elizabeth.fedisleep.api.nodeinfo.NodeInfoApi
import sh.elizabeth.fedisleep.util.SupportedInstances
import javax.inject.Inject

class MetaRemoteDataSource @Inject constructor(
    private val nodeInfoApi: NodeInfoApi,
) {
    suspend fun getInstanceData(instance: String): Pair<String, SupportedInstances>? {
        val (delegatedInstance, nodeInfoHref) = nodeInfoApi.getDelegatedInstanceData(instance)
            ?: return null
        val nodeInfoSoftware = nodeInfoApi.getSoftware(nodeInfoHref) ?: return null

        return delegatedInstance to when (nodeInfoSoftware.name) {
            "iceshrimp" -> SupportedInstances.ICESHRIMP
            else -> return null
        }

        return null
    }
}
