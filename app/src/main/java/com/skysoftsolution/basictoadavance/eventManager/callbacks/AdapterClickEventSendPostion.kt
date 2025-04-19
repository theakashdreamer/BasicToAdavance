package com.skysoftsolution.basictoadavance.teamModules.callbacks

import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor

interface AdapterClickEventSendPostion {

    fun onClickListenerEventReminder(distributor: EventReminder)
    fun onSwitchStatusChanged(distributor: EventReminder, isActive: Boolean)

}