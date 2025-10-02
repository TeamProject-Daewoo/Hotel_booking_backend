package com.example.backend.receipt;

import com.example.backend.reservation.ReservationDto;
import com.example.backend.reservation.ReservationService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ReceiptController {

    private final ReservationService reservationService;

    @GetMapping("/api/receipt/{reservationId}")
    public void downloadReceipt(@PathVariable Long reservationId, HttpServletResponse response) throws IOException {
        ReservationDto reservation = reservationService.findReservationById(reservationId);

        if (reservation == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reservation not found with id: " + reservationId);
            return;
        }

        // 한글 폰트 경로 (Windows 기준)
        String fontPath = "fonts/malgun.ttf";

        BaseFont baseFont;
        try {
            baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            throw new IOException("폰트 로딩 실패: " + e.getMessage(), e);
        }

        // 폰트 스타일 (com.lowagie.text.Font 명시)
        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(baseFont, 18, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font labelFont = new com.lowagie.text.Font(baseFont, 12, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font valueFont = new com.lowagie.text.Font(baseFont, 12, com.lowagie.text.Font.NORMAL);
        com.lowagie.text.Font boldValueFont = new com.lowagie.text.Font(baseFont, 14, com.lowagie.text.Font.BOLD);

        // 예약 정보 추출
        Long resId = reservation.getReservationId();
        String hotelName = reservation.getHotelName();
        String customerName = reservation.getCustomerName();
        String checkInDate = reservation.getCheckInDate() != null ? reservation.getCheckInDate().toString() : "N/A";
        String checkOutDate = reservation.getCheckOutDate() != null ? reservation.getCheckOutDate().toString() : "N/A";
        Integer totalPrice = reservation.getTotalPrice() != null ? reservation.getTotalPrice() : 0;
        Integer discountPrice = reservation.getDiscountPrice() != null ? reservation.getDiscountPrice() : 0;

        // 응답 헤더 설정
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=receipt_" + resId + ".pdf");

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // 로고 (HotelHub - Hub 초록)
            com.lowagie.text.Font hotelFont = new com.lowagie.text.Font(baseFont, 20, com.lowagie.text.Font.BOLD, Color.BLACK);
            com.lowagie.text.Font greenFont = new com.lowagie.text.Font(baseFont, 20, com.lowagie.text.Font.BOLD, new Color(0, 180, 0));

            Chunk hotelName1 = new Chunk("Hotel", hotelFont);
            Chunk hotelName2 = new Chunk("Hub", greenFont);
            Paragraph logo = new Paragraph();
            logo.add(hotelName1);
            logo.add(hotelName2);
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.setSpacingAfter(12f);
            document.add(logo);

            // 제목
            Paragraph title = new Paragraph("영수증", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(18f);
            document.add(title);

            // 본문 정보 (2열)
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 5});
            table.setSpacingAfter(12f);

            addRow(table, "호텔 ID", String.valueOf(resId), labelFont, valueFont);
            addRow(table, "숙소 이름", hotelName != null ? hotelName : "N/A", labelFont, valueFont);
            addRow(table, "예약자 이름", customerName != null ? customerName : "N/A", labelFont, valueFont);
            addRow(table, "체크인", checkInDate, labelFont, valueFont);
            addRow(table, "체크아웃", checkOutDate, labelFont, valueFont);

            document.add(table);

            // 수평선 (텍스트로 대체)
            Paragraph hr1 = new Paragraph("------------------------------------------------------------------------------------------------------------------------");
            hr1.setSpacingBefore(8f);
            hr1.setSpacingAfter(8f);
            document.add(hr1);

            // 할인 금액 (오른쪽 정렬)
            PdfPTable discountTable = new PdfPTable(2);
            discountTable.setWidthPercentage(100);
            discountTable.setWidths(new int[]{2, 5});
            addRow(discountTable, "할인 금액", String.format("%,d 원", discountPrice), labelFont, valueFont, true);
            document.add(discountTable);

            // 수평선 (텍스트로 대체)
            Paragraph hr2 = new Paragraph("------------------------------------------------------------------------------------------------------------------------");
            hr2.setSpacingBefore(8f);
            hr2.setSpacingAfter(8f);
            document.add(hr2);

            // 총 결제 금액 (굵게, 오른쪽 정렬)
            PdfPTable priceTable = new PdfPTable(2);
            priceTable.setWidthPercentage(100);
            priceTable.setWidths(new int[]{2, 5});
            addRow(priceTable, "총 결제 금액", String.format("%,d 원", totalPrice), labelFont, boldValueFont, true);
            document.add(priceTable);

            document.add(Chunk.NEWLINE);

            // 하단 정보
            Paragraph footer = new Paragraph();
            footer.add(new Paragraph("발행일: " + LocalDate.now(), valueFont));
            footer.add(new Paragraph("영수증 번호: MOCK-" + resId, valueFont));
            footer.setSpacingBefore(14f);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            throw new IOException("PDF 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // 기본 (왼쪽 정렬)
    private void addRow(PdfPTable table, String label, String value, com.lowagie.text.Font labelFont, com.lowagie.text.Font valueFont) {
        addRow(table, label, value, labelFont, valueFont, false);
    }

    // 정렬 옵션 포함 (오른쪽 정렬 필요시 rightAlign = true)
    private void addRow(PdfPTable table, String label, String value, com.lowagie.text.Font labelFont, com.lowagie.text.Font valueFont, boolean rightAlign) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(6);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(6);
        cell2.setHorizontalAlignment(rightAlign ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
        table.addCell(cell2);
    }
}
