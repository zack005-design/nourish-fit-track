package com.fitnessapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthConnectManagerTest {

    @Test
    fun testWriteDataToHealthConnectWithZeroRecords() {
        val result = HealthConnectManager.writeDataToHealthConnect(0, 0, 0)
        assertEquals("No local records available to sync to Google Health.", result)
    }

    @Test
    fun testWriteDataToHealthConnectWithRecords() {
        val result = HealthConnectManager.writeDataToHealthConnect(3, 5, 2)
        assertTrue(result.contains("Synced 10 items to Google Health Connect"))
        assertTrue(result.contains("3 nutrition logs"))
        assertTrue(result.contains("5 water entries"))
        assertTrue(result.contains("2 sleep sessions"))
    }

    @Test
    fun testReadDataFromHealthConnectWithoutContext() {
        val result = HealthConnectManager.readDataFromHealthConnect(null)
        assertTrue(result.contains("Health Connect data sync active"))
    }

    @Test
    fun testHealthConnectPermissionsCount() {
        assertEquals(8, HealthConnectManager.HEALTH_CONNECT_PERMISSIONS.size)
    }
}
