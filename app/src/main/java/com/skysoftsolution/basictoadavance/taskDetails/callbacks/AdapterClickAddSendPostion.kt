package com.skysoftsolution.basictoadavance.teamModules.callbacks

import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder

interface AdapterClickAddSendPostion {

    fun onClickListenerEventReminder(events: EventReminder)
    fun onSwitchStatusChanged(events: EventReminder, isActive: Boolean)
}