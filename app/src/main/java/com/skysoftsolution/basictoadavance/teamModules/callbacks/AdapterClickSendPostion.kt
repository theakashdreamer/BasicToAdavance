package com.skysoftsolution.basictoadavance.teamModules.callbacks

import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor

interface AdapterClickSendPostion {

    fun onClickListenerDistributor(distributor: Distributor)
    fun onSwitchStatusChanged(distributor: Distributor, isActive: Boolean)

}