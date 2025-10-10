|List| Repository | Link. |
|------|------|------|
|사용자 프론트엔드| 🌐 **Hotel_booking_frontend (Vue 3)** | [![User-Frontend Repo](https://img.shields.io/badge/GitHub-Hotel_booking_frontend-181717?logo=github)](https://github.com/TeamProject-Daewoo/Hotel_booking_frontend) |
|사용자 백엔드 ⭐ 배포 메인| 🌐 **Hotel_booking_backend (Spring Boot)** | [![User-Frontend Repo](https://img.shields.io/badge/GitHub-Hotel_booking_backend-181717?logo=github)](https://github.com/TeamProject-Daewoo/Hotel_booking_backend) |
|사업자 프론트엔드| 🌐 **BUS-Frontend (Vue 3)** | [![Business-Frontend Repo](https://img.shields.io/badge/GitHub-BUS--Frontend-181717?logo=github)](https://github.com/TeamProject-Daewoo/BUS-Frontend) |
|관리자 프론트엔드| 🌐 **ADMIN-Frontend (Vue 3)** | [![Admin-Frontend Repo](https://img.shields.io/badge/GitHub-ADMIN--Frontend-181717?logo=github)](https://github.com/TeamProject-Daewoo/ADMIN-Frontend) |
|사업자+관리자 백엔드| ⚙️ **Management-Backend (Spring Boot)** | [![Management-Backend Repo](https://img.shields.io/badge/GitHub-Management--Backend-181717?logo=github)](https://github.com/TeamProject-Daewoo/Management-Backend) |


# 🏨 Hotelhub

> 다양한 사용자 그룹(일반 사용자, 사업자, 관리자)을 위한 **올인원 호텔 예약 생태계**  
> 직관적인 검색부터 예약, 결제, 사후 관리까지  
> 전 과정을 하나의 플랫폼에서 제공하는 **종합 호텔 예약 솔루션**

---

<img width="1883" height="836" alt="image" src="https://github.com/user-attachments/assets/3d0f2944-0e46-4edc-8ffb-3c14074b4805" />


## 🌿 프로젝트 소개
**Hotelhub**은 호텔 예약 서비스의 모든 과정을 통합한 플랫폼으로,  
사용자, 사업자, 관리자 각각의 입장에서 필요한 기능을 하나의 생태계 안에서 제공하는 웹 서비스입니다.  
호텔 검색, 예약, 결제, 리뷰 관리 등 호텔 운영과 이용의 모든 단계를 효율적으로 관리할 수 있습니다.

---

## ✨ 주요 기능

### 👤 사용자
- 🔍 호텔 검색 및 상세 조회  
- 🏨 객실 예약 및 취소  
- 💳 **Toss Payments API**를 이용한 안전한 결제  
- 🧾 리뷰 작성 및 포인트 적립  
- 🎟️ 쿠폰 적용 및 포인트 결제  
- 💬 사용자 문의 등록

### 🏢 사업자
- 🏨 호텔 등록 및 정보 관리  
- 💰 특별가 설정 및 판매 관리  
- ⚠️ 리뷰 신고 기능  
- 📊 매출 통계 조회  

### 🛠 관리자
- 📈 매출 통계 대시보드  
- ✅ 사업자 승인 / 차단 관리  
- ⚠️ 리뷰 신고 관리  
- 📢 공지사항 등록  
- 💬 문의 답변 처리  
- 🎟️ 쿠폰 등록 및 관리  
- 👥 관리자 계정 생성 및 삭제  

---

## 👥 팀원 소개

| 이름 | 역할 | 담당 |
|------|------|------|
| **이용석** | 팀장 | 역할 분배, PPT 제작, 발표, 배포, 로그인 및 회원가입, 권한 처리 |
| **김용성** | 팀원 | 검색, 찜, 메인 페이지 기능, 리뷰 관리, 관리자 대시보드 |
| **원동건** | 팀원 | 상세 페이지, 사업자 페이지 UI 및 기능 |
| **이동현** | 팀원 | 호텔 데이터 API 동기화, 문의, 공지사항, 쿠폰 기능, 헤더 |
| **이승엽** | 팀원 | 결제(예약), 리뷰, 메인 페이지 UI, 포인트 결제 기능, 디자인 통일 |

---

## 🛠 기술 스택

### 💻 Frontend
- Vue 3  
- Vite  
- Vue Router  
- Pinia  
- Axios  

### ⚙️ Backend
- Spring Boot  
- JPA  
- MySQL  

### 🔗 API
- Toss Payments API  
- Kakao Login  
- Kakao Map API  
- Tour API 4.0  

### ☁️ Infra
- AWS EC2 / RDS / S3  
- Nginx  
- Docker / Docker Compose  
- GitHub Actions  
- ECR  

---

## 🚀 배포 및 CI/CD

- GitHub Actions를 통한 **dispatch 트리거**를 사용하여  
  `Hotel_booking_backend` 레포지토리에서 최종 배포 자동화  
- 각 Repository를 **Docker 이미지화**하여 **AWS ECR**에 저장  
- **EC2 서버**에서 `docker-compose` 실행을 통해 컨테이너 배포  
- **Nginx 리버스 프록시**를 통해 프론트엔드와 백엔드 트래픽 분리  
- Docker 기반 **무중단 배포 환경** 구성  

---
