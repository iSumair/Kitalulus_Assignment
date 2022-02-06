package com.sumair.fetchcountrydetails.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface ExRatesInfoRepository extends JpaRepository<ExRatesInfo, Integer>{


}
