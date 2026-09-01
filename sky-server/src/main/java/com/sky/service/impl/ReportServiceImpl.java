package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO getTurnoverStatics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateTimeList(begin, end);
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap<String, Object> map = new HashMap<>();
            map.put("begintime", begintime);
            map.put("endtime", endtime);
            map.put("status",5);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null? 0.0:turnover;
            turnoverList.add(turnover);
        }
        String datetime = StringUtils.join(dateList, ",");
        String turnover = StringUtils.join(turnoverList, ',');

        return TurnoverReportVO.builder().dateList(datetime).turnoverList(turnover).build();
    }

    @Override
    public UserReportVO getUserStatics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateTimeList(begin, end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for(LocalDate date : dateList) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap<String, Object> map = new HashMap<>();
            map.put("endtime",endtime);
            Integer totalUser = userMapper.getByMap(map);
            map.put("begintime",begintime);
            Integer newUser = userMapper.getByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);


        }

        return UserReportVO.builder().dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateTimeList(begin, end);
        List<Integer> validOrderList = new ArrayList<>();
        List<Integer> totalOrderList = new ArrayList<>();
        for(LocalDate date : dateList) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap<String, Object> map = new HashMap<>();
            map.put("begintime", begintime);
            map.put("endtime", endtime);
            Integer totalOrder = orderMapper.countByMap(map);
            map.put("status", Orders.COMPLETED);
            Integer validOrder = orderMapper.countByMap(map);
            validOrderList.add(validOrder);
            totalOrderList.add(totalOrder);
        }
        int totalorder = totalOrderList.stream().mapToInt(Integer::intValue).sum();
        int validorder = validOrderList.stream().mapToInt(Integer::intValue).sum();
        double rate =0.0;
        if (totalorder!=0) {
            rate = validorder*1.0/totalorder;
        }
        return OrderReportVO.builder().dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .totalOrderCount(totalorder)
                .validOrderCount(validorder)
                .orderCompletionRate(rate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime begintime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = orderMapper.getTop10(begintime, endtime);
        List<String> nameList = top10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = top10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder().nameList(StringUtils.join(nameList, ",")).numberList(StringUtils.join(numberList, ",")).build();
    }

    @Override
    public void export(HttpServletResponse response) {
        LocalDate datebegin = LocalDate.now().minusDays(30);
        LocalDate dateend = LocalDate.now().plusDays(1);
        LocalDateTime begintime = LocalDateTime.of(datebegin, LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(dateend, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(begintime, endtime);
        InputStream in =this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(in);
            XSSFSheet sheet = xssfWorkbook.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间:"+datebegin+"至"+dateend);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            XSSFRow row1 = sheet.getRow(4);
            row1.getCell(2).setCellValue(businessData.getValidOrderCount());
            row1.getCell(4).setCellValue(businessData.getUnitPrice());


            for (int i = 0; i < 30; i++) {
                LocalDate date = datebegin.plusDays(i);
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row2 = sheet.getRow(7 + i);
                row2.getCell(1).setCellValue(date.toString());
                row2.getCell(2).setCellValue(businessData.getTurnover());
                row2.getCell(3).setCellValue(businessData.getValidOrderCount());
                row2.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row2.getCell(5).setCellValue(businessData.getUnitPrice());
                row2.getCell(6).setCellValue(businessData.getNewUsers());
            }

            ServletOutputStream outputStream = response.getOutputStream();
            xssfWorkbook.write(outputStream);
            outputStream.close();
            xssfWorkbook.close();




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<LocalDate> getDateTimeList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(begin.isBefore(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
