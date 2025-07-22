package com.skysoftsolution.basictoadavance.eventManager.callbacks

import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder

interface AdapterClickEventSendPostion {

    fun onClickListenerEventReminder(distributor: EventReminder)
    fun onSwitchStatusChanged(distributor: EventReminder, isActive: Boolean)

}