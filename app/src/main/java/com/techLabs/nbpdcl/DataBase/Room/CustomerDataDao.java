package com.techLabs.nbpdcl.DataBase.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomerDataDao {

    @Insert
    void insert(CustomerData data);

    @Insert
    void insertAll(List<CustomerData> list);

    @Query("DELETE FROM ConsumerDetails WHERE customerNumber = :customerNumber")
    void deleteByCustomerNumber(String customerNumber);

    @Query("DELETE FROM ConsumerDetails")
    void deleteAll();

    @Query("""
                SELECT * FROM ConsumerDetails
                WHERE customerNumber = :customerNumber
                ORDER BY phase
            """)
    List<CustomerData> getByCustomerNumber(String customerNumber);

    @Query("SELECT * FROM ConsumerDetails ORDER BY customerNumber, phase")
    List<CustomerData> getAll();

    @Query("""
            SELECT * FROM ConsumerDetails 
            GROUP BY customerNumber
            """)
    List<CustomerData> getUniqueCustomers();
}
