package com.xxmrk888ytxx.portal.providedContract.logsScreen

import com.xxmrk888ytxx.coreandroid.AndroidLogger
import com.xxmrk888ytxx.logsscreen.contract.ProvideLogsContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProvideLogsContractImpl @Inject constructor() : ProvideLogsContract {
    override val logs: Flow<List<Pair<Long,String>>> = AndroidLogger.logs
}