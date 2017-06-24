package com.jt.manage.service;

import org.springframework.stereotype.Service;

import com.jt.common.spring.exetend.PropertyConfig;

@Service
public class PropertyService {
	@PropertyConfig
	public String REPOSITORY_PATH;
	
	@PropertyConfig
	public String IMAGE_BASE_URL;
}
