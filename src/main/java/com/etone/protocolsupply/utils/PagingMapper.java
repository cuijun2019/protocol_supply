package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.PageStatisticsDto;
import com.etone.protocolsupply.model.dto.PagingDto;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class PagingMapper {
    public void storeMappedInstanceBefore(Object source, Object target, HttpServletRequest request) {
        if (target instanceof PagingDto && source instanceof Page) {
            PagingDto targetDto = (PagingDto) target;
            Page sourceModel = (Page) source;
            if (sourceModel.getTotalPages() >= 0) {
                int currentPage = (int) sourceModel.getPageable().getOffset() / sourceModel.getPageable().getPageSize() + 1;
                PageStatisticsDto statistics = targetDto.getStatistics();
                statistics.setPageSize(sourceModel.getPageable().getPageSize());
                statistics.setCurrentPage(currentPage < 1 ? 1 : currentPage);
                statistics.setTotalPages(sourceModel.getTotalPages());
                invoke(targetDto, request);
            }
        }
    }

    private void invoke(PagingDto dto, HttpServletRequest request) {
        String self;
        String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + request.getRequestURI();
        try {
            Map<String, String> paramMap = getQueryParams(request.getQueryString());
            paramMap.remove("pageSize");
            paramMap.remove("currentPage");
            String paramString = URLDecoder.decode(mapToString(paramMap), "UTF-8");
            PageStatisticsDto statistics = dto.getStatistics();
            String page = "pageSize=" + String.valueOf(statistics.getPageSize());

            if (this.hasPreviousLink(dto)) {
                self = baseURL + "?" + page + "&currentPage=" + String.valueOf(statistics.getCurrentPage() - 1) + (Strings.isNotBlank(paramString) ? "&" : "") + paramString;
                dto.setPrev(self);
            }

            if (this.hasNextLink(dto)) {
                self = baseURL + "?" + page + "&currentPage=" + String.valueOf(statistics.getCurrentPage() + 1) + (Strings.isNotBlank(paramString) ? "&" : "") + paramString;
                dto.setNext(self);
            }

            self = baseURL + "?" + page + "&currentPage=" + String.valueOf(statistics.getCurrentPage()) + (Strings.isNotBlank(paramString) ? "&" : "") + paramString;
            dto.setSelf(self);
        } catch (UnsupportedEncodingException e) {
            throw new GlobalServiceException(GlobalExceptionCode.SERVICE_ERROR.getCode(), "url encode fail");
        }

    }

    /**
     * 把查询条件String转map
     *
     * @param queryString
     * @return
     */
    private Map<String, String> getQueryParams(String queryString) {
        Map<String, String> map = new HashMap<>();

        if (Strings.isNotBlank(queryString)) {
            String[] arr = queryString.split("&");
            String[] param;

            for (String str : arr) {
                param = str.split("=");
                map.put(param[0], param[1]);
            }
        }

        return map;
    }

    /**
     * 把map转String，用于拼接url
     *
     * @param paramMap
     * @return
     */
    private String mapToString(Map<String, String> paramMap) {
        String paramString = "";
        for (String key : paramMap.keySet()) {
            paramString += "&" + key + "=" + paramMap.get(key);
        }
        return Strings.isNotBlank(paramString) ? paramString.substring(1) : "";
    }

    private boolean hasPreviousLink(PagingDto dto) {
        return dto.getStatistics().getCurrentPage() > 1;
    }

    private boolean hasNextLink(PagingDto dto) {
        PageStatisticsDto statistics = dto.getStatistics();
        return statistics.getTotalPages() == null || statistics.getCurrentPage() < statistics.getTotalPages();
    }
}
