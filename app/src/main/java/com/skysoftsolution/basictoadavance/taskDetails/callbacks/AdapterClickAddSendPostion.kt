package com.skysoftsolution.basictoadavance.teamModules.callbacks

import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder

interface AdapterClickAddSendPostion {

    fun onClickListenerEventReminder(distributor: EventReminder)
    fun onSwitchStatusChanged(distributor: EventReminder, isActive: Boolean)
}