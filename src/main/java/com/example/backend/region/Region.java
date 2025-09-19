package com.example.backend.region;

import com.example.backend.common.HangulUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "regions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"code", "area_code"})
})
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;       // 지역명 (예: 서울특별시, 강남구)

    @Column(nullable = false)
    private int code;          // Tour API의 지역/시군구 코드

    @Column(name = "area_code")
    private Integer areaCode;

    @Column(name = "name_chosung") // 초성 저장을 위한 컬럼 추가
    private String nameChosung;

    // 데이터를 생성할 때 사용할 생성자
    public Region(String name, int code, Integer areaCode) {
        this.setName(name);
        this.code = code;
        this.areaCode = areaCode;
    }
    public void setName(String name) {
        this.name = name;
        this.nameChosung = HangulUtils.getChosung(name); // HangulUtils 호출
    }
}
