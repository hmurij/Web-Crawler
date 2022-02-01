package com.webcrawler.dao;

import com.webcrawler.model.PageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageInfoRepository extends JpaRepository<PageInfo, String> {

}
