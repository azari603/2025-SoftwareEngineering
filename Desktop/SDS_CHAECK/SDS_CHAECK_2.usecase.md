


# Use Case #<1> : <회원가입>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <신규 사용자가 이메일(또는 소셜 로그인 연동용 식별자)과 기본 정보를 입력해 계정을 생성한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User(비회원 사용자)> |
| **Preconditions** | <시스템이 정상 동작 중이어야 하며, 중복 가입 검증 및 이메일 인증 발송 기능이 준비돼 있어야 한다.> |
| **Trigger** | <사용자가 “회원가입” 버튼을 클릭한다.> |
| **Success Post Condition** | <신규 계정이 DB에 생성되고, 선택한 방식(이메일 인증/소셜 연동)이 완료되면 로그인 화면으로 이동한다.> |
| **Failed Post Condition** | <유효성 검증 실패, 중복 계정, 서버/DB 오류 등으로 가입이 완료되지 않고 오류 메시지를 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 회원가입을 진행한다.> |
| **1** | <사용자가 이메일 회원가입일 시, 아이디, 이메일, 비밀번호, 비밀번호 확인 소셜 연동을 통한 회원가입일 시 아이디, 이용약관/개인정보처리방침 동의 여부를 입력한다.> |
| **2** | <시스템이 입력값 유효성(형식/중복/약관 동의)을 검사한다.> |
| **3** | <시스템이 가입하는 이메일에 인증 메일을 발송한다.> |
| **4** | <사용자가 인증 링크를 클릭하면 시스템이 계정을 활성화한다.> |
| **5** | <회원가입 완료 안내 메시지와 함께 로그인하러 가기 버튼이 활성화된다.> |
| **6** | <사용자가 버튼을 눌러 로그인 페이지 이동> |   

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 아이디 중복 또는 형식 불일치**<br>…**2a1.** 중복 시 “이미 사용 중인 아이디입니다.” 메시지 출력<br>…**2a2.** 형식 불일치 시 올바른 형식을 안내하는 메시지 출력 |
|  | **2b. 이메일 중복 또는 형식 불일치**<br>…**2b1.** 중복 시 “이미 사용 중인 이메일입니다.” 메시지 출력<br>…**2b2.** 형식 불일치 시 올바른 형식을 안내하는 메시지 출력 |
|  | **2c. 비밀번호 형식 불일치**<br>…**2c1.** “비밀번호 형식이 올바르지 않습니다.” 메시지 출력 |
|  | **2d. 비밀번호 확인 불일치**<br>…**2d1.** “비밀번호 확인이 불일치합니다.” 메시지 출력 |
| **3** | **3a. 메일 발송 실패함**<br>…**3a1.** “인증 메일 발송에 실패했습니다” 메시지 및 재발송 옵션 제공 |
| **4** | **4a. 인증 토큰오류**<br>…**4a1.** “유효하지 않은 인증 요청” 메시지, 재발송 유도 |

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds (메일 수신은 네트워크 상황에 따라 상이) |
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<2> : <로그인>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 아이디/비밀번호(또는 소셜)로 인증하여 서비스에 접속한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User(회원 사용자)> |
| **Preconditions** | <가입이 완료되어 있어야 한다. 계정이 탈퇴 상태가 아니어야 한다.> |
| **Trigger** | <사용자가 아이디와 비밀번호 입력 후 “로그인” 버튼을 클릭한다.<br>또는 소셜 아이콘을 눌러 소셜페이지에서 로그인 절차를 수행한다.
> |
| **Success Post Condition** | <인증 성공 시 세션/토큰이 발급되고, 사용자의 개인화 영역(추천, 나의 서재, 알림 등)이 활성화된다.> |
| **Failed Post Condition** | <인증 실패(자격 증명 불일치/탈퇴), 서버/DB 오류 시 로그인에 실패하고 오류 메시지를 표시한다> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 로그인을 수행한다.> |
| **1** | <사용자가 아이디/비밀번호를 입력(또는 소셜 버튼 선택)한다.> |
| **2** | <시스템이 자격 증명을 검증한다.> |
| **3** | <성공 시 세션/토큰을 생성하고 사용자 정보를 로드한다.> |
| **4** | <메인홈 또는 사용자가 마지막에 머무른 페이지로 이동한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 아이디/비밀번호가 일치하지 않는 경우**<br>…**2a1.**  “아이디 혹은 비밀번호가 일치하지 않습니다.” 라는 메시지를 출력한다.|
|  | **2b. 소셜 인증 실패**<br>…**2b1.**  ”로그인이 완료되지 않았습니다.”라는 메시지를 출력하고, 로그인 화면으로 복귀한다.|
| **3** | **3a. 서버/네트워크 오류**<br>…**3a1.** “정보 불러오기에 실패했습니다.”라는 오류 메시지 표시하고, 로그인 화면으로 복귀한다. |

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 second(인증 응답) |
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<3> : <로그아웃>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <로그인된 사용자가 세션을 종료한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User(회원 사용자)> |
| **Preconditions** | <사용자가 로그인 상태여야 한다.> |
| **Trigger** | <헤더의 프로필을 선택 후 “로그아웃” 선택.> |
| **Success Post Condition** | <세션/토큰이 무효화되고 비회원 상태로 메인 화면으로 이동한다.> |
| **Failed Post Condition** | <서버 오류로 세션 만료 처리 실패 시, 사용자에게 오류 메시지를 표시하되 민감 정보는 즉시 화면에서 제거한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 로그아웃을 수행한다.> |
| **1** | <사용자가 “로그아웃”을 클릭한다.> |
| **2** | <시스템이 세션/토큰을 무효화한다.> |
| **3** | <시스템이 메인 화면(비회원 뷰)로 이동시킨다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a.네트워크 오류**<br>…**2a1.** 로컬 세션 삭제 후 “완료되지 않은 로그아웃 재시도” 안내.|
| **3** | **3a.서버/네트워크 오류**<br>…**3a1.** 오류 메시지 표시.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 second |
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<4> : <아이디 찾기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 가입 시 등록한 이메일을 통해 아이디를 찾는다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User(회원 사용자)> |
| **Preconditions** | <회원가입시에 사용한 이메일이 사용 가능해야 한다.> |
| **Trigger** | <로그인 페이지에서 “아이디 찾기” 클릭.> |
| **Success Post Condition** | <가입 이메일로 일부 마스킹된 아이디를 발송받는다.> |
| **Failed Post Condition** | <일치하는 계정이 없을 경우 안내 메시지를 표시하고 이메일을 발송하지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 아이디 찾기를 진행한다.> |
| **1** | <사용자가 가입시 사용한 이메일를 입력한다.> |
| **2** | <시스템이 아이디가 가입시 사용된 이메일인지 확인한다.> |
| **3** | <가입 이메일로 일부 마스킹된 아이디를 발송받는다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a.입력된 이메일의 형식이 불일치**<br>…**1a1.** “이메일 형식이 일치하지 않습니다.”라는 메시지를 표시한다.|
| **2** | **2a.입력된 이메일이 등록된 이메일이 아닌 경우**<br>…**2a1.** “해당 이메일은 존재하지 않습니다.”라는 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds (검증 응답) |
| **Frequency** | 요청 시 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<5> : <비밀번호 찾기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 이메일을 통해 임시 비밀번호를 전송받는다> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User(회원 사용자)> |
| **Preconditions** | <해당 이메일의 계정이 존재해야 한다. 메일 발송 기능이 정상이어야 한다.> |
| **Trigger** | <로그인 페이지에서 “비밀번호 찾기” 클릭.> |
| **Success Post Condition** | <해당 이메일로 임시 비밀번호가 발송되고, 임시 비밀번호가 DB에 반영된다.<br>사용자는 로그인 화면으로 이동 후 임시 비밀번호로 로그인이 가능하다.> |
| **Failed Post Condition** | <인증 실패/토큰 만료/서버 오류 시 재설정이 완료되지 않으며 오류 메시지를 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 비밀번호 찾기를 진행한다.> |
| **1** | <사용자가 이메일과 아이디를 입력한다.> |
| **2** | <시스템이 계정 존재 여부 확인 후 임시 비밀번호를 발급하고 비밀번호를 업데이트한다.> |
| **3** | <사용자가 이메일을 확인 후 새로 발급된 비밀번호로 로그인한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a.아이디와 이메일이 일치하지 않는 경우**<br>…**2a1.**“아이디와 이메일이 일치하지 않습니다.”라는 메시지를 표시한다.|
| **3** | **3a.DB에서 임시 비밀번호로 변경 실패 시**<br>…**3a1.**“임시 비밀번호 변경에 실패했습니다.”라는 메시지를 표시하고, 임시 비밀번호를 재설정 후 이메일을 재전송한다. |

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** |  링크 발송 <= 2 seconds(서버 처리 기준) |
| **Frequency** | 요청 시 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<6> : <계정 정보 변경>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 계정 설정에서 계정 정보(이메일, 비밀번호)를 변경한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User (로그인한 사용자)> |
| **Preconditions** | <로그인 상태이며 계정 설정 변경 화면으로 이동한 상태여야 한다.> |
| **Trigger** | <계정 설정 페이지의 계정 정보 변경 창에서 계정 정보를 변경 후 저장 버튼을 클릭.> |
| **Success Post Condition** | <변경 사항이 DB에 저장되고, 관련 기능에 즉시 반영된다.> |
| **Failed Post Condition** | <유효성 실패나 서버/DB 오류 시 저장되지 않고 오류 메시지를 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 계정 설정 페이지에서 계정 정보를 변경한다.> |
| **1** | <사용자가 설정 항목(이메일/비밀번호)을 변경한 후 저장 버튼을 클릭한다.> |
| **2** | <시스템이 변경값을 검증한다.> |
| **3** | <시스템이 DB에 변경사항을 저장한다.> |
| **4** | <화면에 저장 성공 메시지 및 즉시 반영된 상태를 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 이메일 중복 또는 형식 불일치**<br>…**2a1.** 이메일 중복 시 “이미 사용중인 이메일입니다.”라는 오류 메시지를 출력한다.<br>…**2a2.** 형식 불일치 시 올바른 형식을 안내하는 메시지를 출력한다. |
|  | **2b.  비밀번호 형식 불일치**<br>…**2b1.**  “비밀번호 형식이 올바르지 않습니다.”라는 오류 메시지를 출력한다.|
| **3** | **3a. DB 오류**<br>…**3a1.** “저장에 실패했습니다” 메시지를 표시하고, 이전 값 유지 |

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds |
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<7> : <계정 탈퇴>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 계정 설정 페이지에서 계정을 탈퇴한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <박정호> |
| **Last Update** | <2025.10.28> |
| **Status** | <Draft> |
| **Primary Actor** | <User (로그인한 사용자)> |
| **Preconditions** | <로그인 상태여야 한다.> |
| **Trigger** | <이용자가 계정 설정 페이지에서 계정 탈퇴 버튼을 클릭한다.> |
| **Success Post Condition** | <DB에서 계정의 상태를 삭제 상태로 하고, 로그아웃된 화면으로 이동한다.> |
| **Failed Post Condition** | <계정 탈퇴에 실패하고 DB에서 계정의 상태를 변경하지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 계정 탈퇴 버튼을 클릭한다.> |
| **1** | <사용자가 탈퇴 확인 용으로 비밀번호를 입력한다.> |
| **2** | <시스템이 해당 비밀번호가 일치하는지 확인한다.> |
| **3** | <시스템이 DB에서 현재 계정의 상태를 삭제상태로 변경한다.> |
| **4** | <사용자가 로그아웃된 상태로 메인화면으로 이동한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 일치하지 않는 비밀번호인 경우**<br>…**2a1.**“비밀번호가 일치하지 않습니다.”라는 메시지를 출력하고, 계정 설정 페이지로 돌아간다.|
| **3** | **3a. DB 오류**<br>…**3a1.** “계정 탈퇴에 실패했습니다.”라는 오류메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds |
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<8> : <책 검색하기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 도서명, 저자를 키워드로 원하는 책을 검색한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <송서현> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원/비회원 모두 가능)> |
| **Preconditions** | <도서정보시스템에 도서 데이터가 등록되어 있어야 한다.> |
| **Trigger** | <사용자가 검색창에 키워드를 입력하고 엔터를 누르거나 검색 아이콘을 클릭한다.> |
| **Success Post Condition** | <검색 결과가 존재할 경우, 일치하는 도서 목록이 표시된다.<br>검색 결과가 없을 경우, “검색 결과가 없습니다.”라는 메시지를 표시한다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패 등으로 검색 자체가 수행되지 못한 경우 오류 메시지를 출력하고 검색에 실패한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 도서를 검색한다.> |
| **1** | <사용자가 검색창에 키워드(제목, 저자)를 입력한 후 엔터나 검색 버튼을 눌렀을 때 실행된다.> |
| **2** | <도서 검색 시스템은 검색 키워드를 기반으로 DB에서 도서를 검색한다.> |
| **3** | <시스템이 검색 결과를 사용자에게 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 네트워크나 서버 오류 발생할 경우**<br>…**2a1.**오류 메시지를 화면에 출력한다.|
| **3** | **3a. 일치하는 책/저자가 없을 경우**<br>…**3a1.** “검색 결과가 없습니다.” 메시지를 화면에 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds |
| **Frequency** | 상시 사용 |
| **Concurrency** | 다수의 사용자가 동시에 검색 가능|
| **Due Date** | <2025.11.07> |

---

# Use Case #<9> : <책 정보 상세 조회>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 검색 결과나 추천 목록에서 특정 책을 선택해 상세 정보를 확인한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <송서현> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원/비회원 모두 가능)> |
| **Preconditions** | <검색 결과나 추천 목록이 표시되어 있다.> |
| **Trigger** | <사용자가 목록에서 도서를 클릭하거나, “나의 서재” 페이지에서 도서를 선택한다.> |
| **Success Post Condition** | <도서 상세 정보(제목, 저자, 출판사, 설명, 별점 등)가 정상적으로 화면에 출력된다.<br>이미지가 없을 경우 기본 이미지로 대체되어 표시된다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패 등으로 상세 정보를 불러오지 못한 경우 오류 메시지를 화면에 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 해당 책의 상세 정보를 조회한다.> |
| **1** | <사용자가 목록에서 특정 책을 선택한다.> |
| **2** | <시스템이 해당 책의 상세 정보를 DB에서 조회한다.> |
| **3** | <시스템이 도서 상세 페이지를 화면에 출력한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 네트워크나 서버 오류 발생할 경우**<br>…**2a1.**오류 메시지를 화면에 출력한다.|
| **3** | **3a. 해당 책의 이미지가 누락되었을 경우**<br>…**3a1.**기본 이미지로 대체한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds |
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<10> : <추천 책 조회>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <시스템이 기본 추천 또는 개인 맞춤형 추천 도서를 제공한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <송서현> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원/비회원 모두 가능)> |
| **Preconditions** | <시스템이 정상적으로 실행 중이어야 한다.> |
| **Trigger** | <사용자가 사이트의 메인화면에 진입한다.> |
| **Success Post Condition** | <로그인 사용자 -> 개인 맞춤형 추천 도서 목록을 표시한다.<br>비로그인 사용자 -> 기본 인기 도서 목록을 표시한다.<br>추천 데이터가 없을 경우 기본 인기 도서 목록을 표시한다> |
| **Failed Post Condition** | <서버나 추천 알고리즘 오류로 인해 데이터를 불러오지 못한 경우 로그에 에러메시지를 남긴 후, 기본 인기 도서 목록을 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자별 추천 도서 목록을 메인화면에 표시한다.> |
| **1** | <사용자가 사이트의 메인화면에 진입한다.> |
| **2** | <시스템이 로그인 여부를 확인한다.> |
| **3.1** | <로그인된 경우 -> 개인화 추천 로직이 실행된다.> |
| **3.2** | <비로그인 경우 -> 기본 인기 도서 목록을 표시한다.> |
| **4** | <시스템이 추천 목록을 지정 섹션에 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **3** | **3a. 사용자 개인화 데이터가 없을 경우**<br>…**3a1.**기본 인기 도서 목록으로 대체하여 표시한다.|
| **4** | **4a. 추천 엔진 오류가 발생할 경우**<br>…**4a1.**로그에 에러메시지를 남긴 후, 기본 인기 도서 목록으로 대체하여 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds |
| **Frequency** | 사이트 사용 시마다 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<11> : <책BTI 테스트 진행 및 결과 조회>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 책BTI 테스트를 수행하고, 카테고리별 점수를 기반으로 성향 유형을 도출하며, AI 추천 엔진을 통해 해당 유형에 맞는 도서를 자동으로 추천받는다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <송서현> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인된 사용자> |
| **Preconditions** | <시스템이 정상적으로 동작 중이어야 한다.<br>책BTI 문제 세트 및 결과 유형 데이터가 DB에 등록되어 있어야 한다.> |
| **Trigger** | <사용자가 상단 헤더에서 “책BTI“ 버튼을 클릭한다.> |
| **Success Post Condition** | <시스템이 각 문항의 카테고리별 점수를 합산하고, 최고 점수 카테고리에 해당하는 유형을 결정한다.<br>사용자의 책BTI 결과 유형이 화면에 표시된다.<br>AI 추천 엔진이 해당 유형을 기반으로 추천 도서 목록을 생성하여 함께 표시한다.
> |
| **Failed Post Condition** | <DB 또는 AI 서버 오류로 인해 문제 로드, 결과 계산, 추천 생성 중 실패할 경우 오류 메시지를 출력한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 책BTI 검사를 진행한다.> |
| **1** | <사용자가 “책BTI” 버튼을 클릭한다.> |
| **2** | <시스템이 책BTI 문항과 각 문항의 카테고리 정보를 DB에서 불러온다.> |
| **3** | <사용자가 각 문항에 응답한다.> |
| **4** | <시스템이 각 응답을 실시간으로 해당 카테고리 점수에 누적한다.> |
| **5** | <모든 문항 응답이 완료되면, 시스템이 카테고리별 총점을 계산한다.> |
| **6** | <시스템이 가장 높은 점수를 가진 카테고리에 따라 책BTI 결과 유형을 결정한다.> |
| **7** | <AI 추천 엔진이 결과 유형을 기반으로 도서를 추천한다.> |
| **8** | <시스템이 결과 유형과 추천 도서 목록을 함께 화면에 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. DB 연결 오류가 발생한 경우**<br>…**2a1.**오류 메시지를 화면에 출력한다.|
| **5** | **5a. 점수 계산 로직 오류가 발생한 경우**<br>…**5a1.**“AI 추천이 실패했습니다.”라는 오류 메시지를 화면에 출력한다.|
| **7** | **7a. AI 추천 엔진 응답 실패한 경우**<br>…**7a1.** “AI 추천이 실패했습니다.”라는 오류 메시지를 화면에 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 10 seconds|
| **Frequency** | 사용자 1회 이상 응시 가능 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<12> : <도서 상태 지정>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 도서 상세정보 페이지에서 도서의 읽기 상태(읽고 있는 책, 다 읽은 책, 읽고 싶은 책 중 하나)를 선택하여 책의 상태를 저장할 수 있다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <송서현> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인된 사용자> |
| **Preconditions** | <사용자가 로그인되어 있어야한다.<br>도서 상세정보 페이지가 정상적으로 로드되어 있어야 한다.> |
| **Trigger** | <사용자가 책 상세정보 페이지에서 상태 버튼을 클릭한다.> |
| **Success Post Condition** | <사용자가 선택한 읽기 상태가 DB에 저장된다.<br>선택된 버튼에 색깔 변화를 주어 선택되었음을 보여준다.> |
| **Failed Post Condition** | <DB 또는 서버 오류로 인해 상태 변경이나 목록 조회가 실패할 경우 오류 메시지를 화면에 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 책의 상태를 저장한다.> |
| **1** | <사용자가 도서 상세정보 페이지를 연다.> |
| **2** | <사용자가 “읽은 책이에요/ 다 읽었어요 / 읽고 싶어요” 중 하나를 클릭한다.> |
| **3** | <시스템이 로그인 여부를 확인한다.> |
| **4** | <시스템이 선택한 상태를 DB에 저장한다.> |
| **5** | <선택된 상태 버튼의 상태가 변한다.> |


## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **3** | **3a. 로그인하지 않은 사용자인 경우.**<br>…**3a1.**로그인 페이지로 이동한다.|
| **4** | **4a. DB 저장 실패한 경우**<br>…**4a1.**상태를 저장하지 못했다는 오류 메시지를 화면에 출력하고 버튼이 다시 활성화된다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 제한 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<13> : <지정 상태별 책 조회>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <“나의 기록 -> 나의 서재” 페이지에서 사용자가 상태를 저장한 책을 모아볼 수 있다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <송서현> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인된 사용자> |
| **Preconditions** | <사용자가 로그인되어 있어야한다.<br>도서 상세정보 페이지가 정상적으로 로드되어 있어야 한다.> |
| **Trigger** | <사용자가 “나의 서재” 페이지를 클릭한다.> |
| **Success Post Condition** | <“나의 서재” 페이지에서 해당 상태의 도서목록이 DB의 정보를 바탕으로 표시된다.> |
| **Failed Post Condition** | <DB 또는 서버 오류로 인해 목록 조회가 실패할 경우 오류 메시지를 화면에 표시한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 나의 서재 페이지에서 저장된 도서 목록을 조회한다.> |
| **1** | <사용자가 “나의 기록 -> 나의 서재” 페이지로 이동한다.> |
| **2** | <시스템이 DB에서 사용자의 상태별 도서 목록을 조회하여 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 로그인하지 않은 사용자인 경우**<br>…**1a1.**로그인 페이지로 이동한다.|
| **2** | **2a. 해당 상태 도서가 없을 경우**<br>…**2a1.**표시할 도서가 없다는 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 제한 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<14> : <서평 작섣>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 읽은 책에 대한 서평을 작성한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이현승> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인된 사용자> |
| **Preconditions** | <사용자가 로그인 상태여야 한다.> |
| **Trigger** | <사용자가 글쓰기 버튼을 클릭한다.> |
| **Success Post Condition** | <사용자가 서평 작성을 위한 글 입력 및 설정 화면으로 넘어간다.> |
| **Failed Post Condition** | <서평 작성 실패 메시지가 출력되고 메인 화면으로 돌아간다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 글쓰기 버튼을 누르면, 책 선택 화면 후 서평 작성 화면으로 넘어간다. > |
| **1** | <사용자가 서평을 작성할 책을 선택하면, 해당 책에 대한 서평 작성 페이지로 이동한다.> |
| **2** | <작성 페이지에서 사용자는 내용(책을 읽기 시작한 날짜, 다 읽은 날짜, 공개/비공개 여부, 제목, 본문)을 입력한다.> |
| **3** | <사용자가 서평을 다 작성하면 등록버튼을 누른다.> |
| **4** | <시스템이 서평을 DB에 저장하면서 서평 작성이 완료된다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 책에 대한 정보를 불러오는데 오류가 난 경우**<br>…**1a1.**책 정보 불러오기에 실패했다는 메시지를 표시하고 책 선택 화면으로 다시 돌아간다.|
| **3** | **3a. 제목과 내용에 아무 내용이 없는 경우**<br>…**3a1.**제목/내용을 입력해달라는 메시지를 표시하고 작성이 완료되지 않는다.|
| **4** | **4a. 서평 내용을 저장하는데에 실패한 경우**<br>…**4a1.**저장에 실패했다는 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<15> : <다른 사람 서평 보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 다른 이용자가 작성한 서평의 내용을 확인한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이현승> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <모든 사용자> |
| **Preconditions** | <사용자가 작성된 서평 목록을 확인하고 있는 상황이어야 한다.> |
| **Trigger** | <사용자가 다른 사용자의 서평 중에서 조회하고자 하는 서평을 클릭한다.> |
| **Success Post Condition** | <사용자가 다른 사용자가 작성한 서평을 성공적으로 조회한다.> |
| **Failed Post Condition** | <사용자가 작성된 서평을 조회할 수 없다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 서평 목록에서 조회하기 원하는 서평을 클릭한다.> |
| **1** | <사용자가 클릭한 서평의 상세내용을 DB에서 불러온다.> |
| **2** | <서평의 상세내용 페이지에 상세내용을 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 서평 내용을 DB에서 불러오는데 실패한 경우**<br>…**1a1.** 서평 정보를 불러오는데에 실패했다는 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<16> : <다른 상호작용>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 다른 사용자가 작성한 서평에 대해 상호작용한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이현승> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 사용자> |
| **Preconditions** | <사용자가 로그인 상태여야 하며, 서평 상세 내용을 확인 중이어야 한다.> |
| **Trigger** | <사용자가 다른 사용자의 서평 페이지에서 댓글 작성 혹은 좋아요 버튼을 누른다.> |
| **Success Post Condition** | <좋아요를 클릭할 시 표시가 남고, 댓글 작성을 클릭할 시 댓글 작성 창을 표시한다.> |
| **Failed Post Condition** | <좋아요와 댓글이 해당 서평에 남지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 다른 사용자의 서평에 좋아요/댓글 작성 버튼을 클릭한다.> |
| **1** | <사용자가 좋아요 버튼을 클릭할 시 좋아요한 서평으로 저장되고, 댓글 작성 버튼을 클릭할 시 댓글 입력창이 표시된다.> |
| **2** | <사용자가 댓글 작성 완료 후 작성 완료 버튼을 클릭하면 서평의 댓글 화면에 댓글이 남는다.> |
| **3** | <사용자가 좋아요 버튼을 한번 더 클릭하거나 댓글 삭제 버튼을 클릭하면 좋아요 취소 혹은 댓글 삭제가 가능하다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 좋아요 정보를 저장하는 데에 실패 했을 경우**<br>…**1a1.** 좋아요에 실패했다는 메시지를 출력한다.|
| **2** | **2a. 댓글 정보를 저장하는 데에 실패 했을 경우**<br>…**2a1.** 댓글 남기기에 실패했다는 메시지를 출력한다.|
| **3** | **3a. 좋아요 취소/댓글 삭제에 실패 했을 경우**<br>…**3a1.** 좋아요 취소/댓글 삭제에 실패했다는 메시지를 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<17> : <좋아요한 서평 보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 좋아요한 서평 목록을 조회하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이현승> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 사용자> |
| **Preconditions** | <사용자가 로그인 상태여야한다.> |
| **Trigger** | <사용자가 나의 기록 -> 좋아요한 서평 보기 버튼을 클릭한다.> |
| **Success Post Condition** | <사용자가 좋아요를 남긴 서평을 볼 수 있다.> |
| **Failed Post Condition** | <사용자가 좋아요를 남긴 서평이 확인되지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 좋아요를 남긴 서평을 모두 확인한다.> |
| **1** | <사용자는 ‘나의 기록 -> 나의 서평”에서 “좋아요한 서평 보기” 버튼을 클릭한다> |
| **2** | <시스템이 DB에서 사용자가 좋아요 표시한 서평의 내용을 불러온다.> |
| **3** | <좋아요한 서평 보기목록이 표시된다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 좋아요를 남긴 서평이 존재하지 않는 경우**<br>…**2a1.** 좋아요를 남긴 서평이 존재하지 않는다는 메시지를 표시한다.|
|  | **2b. 좋아요를 남긴 서평을 불러오는 데에 실패한 경우**<br>…**2b1.** 좋아요를 남긴 서평 정보를 불러오는 데에 실패했다는 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<18> : <나의 서평 목록 보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 자신이 작성한 서평의 목록을 조회하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이수진> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야한다.> |
| **Trigger** | <사용자가 “나의 기록 -> 나의 서평”을 클릭한다.> |
| **Success Post Condition** | <사용자가 본인이 작성한 서평의 목록을 성공적으로 조회한다.<br>작성된 서평이 존재하지 않는 경우 존재하지 않음을 출력한다.> |
| **Failed Post Condition** | <사용자가 본인이 작성한 서평의 목록을 조회할 수 없다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 ‘나의 기록 -> 나의 서평’에서 자신의 서평 목록을 조회한다.> |
| **1** | <시스템은 DB에서 사용자가 작성한 서평의 목록을 불러온다.> |
| **2** | <시스템은 불러온 서평을 화면에 출력한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 사용자의 ID에 작성된 서평이 존재하지 않는 경우**<br>…**1a1.** “작성된 서평이 존재하지 않습니다.”라는 메시지를 출력하고 빈 목록창을 띄운다.|
|  | **1b. DB 조회 과정에서 오류가 발생한 경우**<br>…**1b1.** “서평 정보를 불러오는데 실패했습니다” 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<19> : <나의 서평 상세 보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 자신이 작성한 서평의 상세 내용을 조회하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이수진> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야 하고, 작성된 서평이 존재해야 한다.> |
| **Trigger** | <사용자가 나의 서평에서 조회하고자 하는 자신의 서평을 클릭한다.> |
| **Success Post Condition** | <사용자가 본인이 작성한 서평의 상세 내용 성공적으로 조회한다.> |
| **Failed Post Condition** | <사용자가 본인이 작성한 서평을 조회할 수 없다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 ‘나의 기록 -> 나의 서평’에서 자신의 특정 서평을 조회한다.> |
| **1** | <시스템은 DB에서 해당 서평의 상세 내용을 조회한다 (제목, 내용, 평점, 작성일, 책 정보 등)> |
| **2** | <시스템은 조회된 서평 정보를 화면에 표시한다> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. DB 조회 과정에서 오류가 발생한 경우**<br>…**1a1.**  “서평 정보를 불러오는데 실패했습니다” 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---
# Use Case #<20> : <나의 서평 수정>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 자신이 작성한 서평의 상세 내용을 수정하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이수진> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야 하며, 수정하려는 서평은 본인 소유여야 한다.> |
| **Trigger** | <사용자가 ‘나의 기록 -> 나의 서평’에서 특정 서평을 선택하고 더보기 아이콘에서 수정 버튼을 클릭한다> |
| **Success Post Condition** | <수정된 서평이 DB에 반영되고 최신 내용이 화면에 표시된다.> |
| **Failed Post Condition** | <수정 내용이 저장되지 않거나, 서버 오류로 인해 변경 내용이 반영되지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 ‘나의 서평’에서 자신의 특정 서평을 수정한다.> |
| **1** | <사용자는 자신의 특정 서평을 선택하고 더보기 아이콘에서 수정 버튼을 클릭하여 수정 페이지로 이동한다.> |
| **2** | <시스템은 선택된 서평의 기존 정보를 수정 화면에 출력한다.> |
| **3** | <사용자는 수정할 항목 (제목, 내용, 공개 여부, 읽은 기간, 평점)을 변경한다.> |
| **4** | <사용자는 ‘저장’ 버튼을 클릭한다.> |
| **5** | <시스템은 입력된 데이터의 유효성을 검사한다.> |
| **6** | <유효성 검사가 통과되면 시스템은 DB에 수정된 정보를 업데이트 하고, 서평 작성일을 수정된 날짜로 변경한다.> |
| **7** | <시스템은 수정 완료 메시지를 표시하고, 수정된 서평 상세 페이지로 이동한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. DB에서 서평의 기존 정보를 불러오기에 실패한 경우**<br>…**2a1.**  시스템은 사용자에게 오류 메시지를 출력한다.|
| **3** | **3a. 사용자가 수정 중 취소 버튼을 클릭한 경우**<br>…**3a1.**  수정 내용은 저장되지 않고 상세 보기 화면으로 돌아간다.|
| **5** | **5a. 제목 또는 내용이 비어있는 경우**<br>…**5a1.**  시스템은 사용자에게 오류 메시지를 출력한다.|
| **6** | **6a. DB 업데이트를 실패한 경우**<br>…**6a1.**  시스템은 사용자에게 수정 실패 메시지를 출력한다|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<21> : <나의 서평 삭제>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 자신이 작성한 서평의 상세 내용을 삭제하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이수진> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야 하며, 삭제하려는 서평은 본인 소유여야 한다.> |
| **Trigger** | <사용자가 ‘나의 기록 -> 나의 서평’에서 특정 서평을 선택하고 더보기 아이콘에서 삭제 버튼을 클릭한다.> |
| **Success Post Condition** | <선택된 서평은 DB에서 삭제되고, 목록 화면에서 해당 서평이 제거된다.> |
| **Failed Post Condition** | <선택된 서평이 삭제되지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 ‘나의 기록 -> 나의 서평’에서 자신의 특정 서평을 삭제한다.> |
| **1** | <사용자는 삭제하고자 하는 자신의 서평을 선택한 후 더보기 아이콘에서 삭제 버튼을 클릭한다.> |
| **2** | <시스템은 사용자가 삭제를 요청한 서평의 고유 ID를 인식한다.> |
| **3** | <시스템은 사용자에게 삭제 확인 팝업 (“삭제하시겠습니까?”)을 출력한다.> |
| **4** | <사용자가 확인을 선택한다.> |
| **5** | <시스템은 DB에서 해당 서평 데이터를 삭제한다.> |
| **6** | <시스템은 삭제 완료 메시지를 출력하고, 나의 서평 목록 보기 페이지로 이동한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **3** | **3a. 사용자가 팝업에서 취소를 클릭한 경우**<br>…**3a1.**  삭제는 수행되지 않고 상세 보기 화면으로 돌아간다.|
| **5** | **5a. DB 삭제 과정에서 오류가 발생한 경우**<br>…**5a1.**  시스템은 오류 메시지를 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<22> : <서평 둘러보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 팔로우한 사용자의 서평 혹은 최근 올라온 서평을 확인할 수 있다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이현승> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <모든 사용자> |
| **Preconditions** | <사용자가 어떤 서평을 확인할지 정해야하며, 팔로우한 사용자의 서평을 확인하는 경우 사용자가 로그인 상태여야 한다.> |
| **Trigger** | <사용자가 헤더의 둘러보기 버튼을 누른다.> |
| **Success Post Condition** | <사용자가 팔로잉 서평/최신 서평 중 하나를 골라서 해당하는 서평의 목록을 볼 수 있다.> |
| **Failed Post Condition** | <사용자가 다른 사용자의 서평 목록을 확인할 수 없다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자는 둘러보기 화면에서 최신 서평 피드나 팔로잉하고 있는 사람들의 서평을 확인할 수 있다.> |
| **1** | <사용자가 둘러보기 버튼을 눌러서 둘러보기 화면으로 넘어간다.> |
| **2** | <사용자가 최신 서평/팔로잉 서평 중에 어떤 서평을 볼지 선택한다.> |
| **3** | <시스템은 DB에서 선택된 종류의 서평목록을 불러온다.> |
| **4** | <사용자는 선택한 종류에 맞는 서평을 둘러볼 수 있다.> |
| **5** | <사용자는 둘러보기 페이지에서 다양한 서평의 작성자, 작성일자, 평점, 서평 제목, 책정보, 댓글 수, 좋아요 수를 확인할 수 있다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **3** | **3a. 선택한 종류에 아무런 서평이 없는 경우**<br>…**3a1.** 작성된 서평이 존재하지 않는다는 메시지를 표시하고 빈 목록을 표시한다.|
|  | **3b. 작성된 서평을 불러오는데에 실패한 경우**<br>…**3b1.** 서평 불러오기에 실패했다는 메시지를 표시한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 4 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<23> : <사용자 팔로우 및 언팔로우>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 다른 사용자를 팔로우 하거나 팔로우를 취소하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이수진> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야 하며, 팔로우 또는 언팔로우 하려는 사용자가 존재하여야 한다.> |
| **Trigger** | <사용자가 다른 사용자의 프로필 화면 또는 서평 상세페이지에서 ‘팔로우’/’언팔로우’ 버튼을 클릭한다.> |
| **Success Post Condition** | <시스템은 요청에 따라 팔로우 관계를 생성하거나 삭제된다.> |
| **Failed Post Condition** | <팔로우 상태가 변경되지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 다른 사용자를 팔로우 하거나 팔로우를 취소한다.> |
| **1** | <시스템은 클릭된 버튼의 현재 상태(팔로잉 여부)를 확인한다.> |
| **2** | <팔로우 상태가 아닌 경우 시스템은 DB에 새로운 팔로우 관계를 생성하고, 팔로잉 대상 사용자에게 팔로우 알림을 전송한다.> |
| **3** | <이미 팔로우 중인 경우 시스템은 DB에서 해당 팔로우 관계를 삭제한다.> |
| **4** | <시스템은 변경된 상태에 따라 버튼 상태를 즉시 갱신한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 서버 및 네트워크 오류가 발생한 경우**<br>…**2a1.**  시스템은 오류 메시지를 출력한다.|
| **3** | **3a. 서버 및 네트워크 오류가 발생한 경우**<br>…**3a1.**  시스템은 오류 메시지를 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<24> : <팔로잉/팔로워 목록 조회>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 자신이 팔로우한 사용자 (팔로잉) 또는 자신을 팔로우한 사용자 (팔로워) 목록을 조회하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <이수진> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야 한다.> |
| **Trigger** | <사용자가 프로필 페이지에서  ‘팔로잉’ 또는 ‘팔로워’ 탭을 클릭한다.> |
| **Success Post Condition** | <선택된 탭에 따라 적절한 사용자 목록(팔로잉/팔로워)이 화면에 표시된다.> |
| **Failed Post Condition** | <사용자 목록이 화면에 표시되지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 프로필 화면에서 ‘팔로잉’ 또는 ‘팔로워’ 목록을 확인한다.> |
| **1** | <시스템은 선택된 탭이 팔로잉인지 팔로워인지 확인한다.> |
| **2** | <시스템은 선택된 탭에 따라 사용자들의 기본 정보를 로드한다. (닉네임, 프로필 이미지 등)> |
| **3** | <시스템은 조회된 사용자 목록을 화면에 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 팔로잉/팔로워가 없는 경우**<br>…**2a1.**  시스템은 화면에 빈 목록을 출력한다.|
|  | **2b. DB 및 네트워크 오류가 발생한 경우**<br>…**2b1.**  시스템은 오류 메시지를 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 알 수 없음 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<25> : <프로필 수정>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 프로필의 유저사진, 배경사진, 닉네임, 소개를 변경하는 기능> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <정도희> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <로그인한 User> |
| **Preconditions** | <사용자가 로그인 상태여야 한다.> |
| **Trigger** | <사용자가 프로필 수정페이지에서 내용을 수정한 후 저장버튼을 누른다.<br>DB가 사용자 프로필을 저장하고 있다.> |
| **Success Post Condition** | <시스템은 수정된 정보를 DB에 업데이트하고, 프로필 변경사항이 프로필 화면에 반영된다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패 등으로 프로필 수정이 수행되지 못한 경우 오류 메시지를 출력하고 검색에 실패한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 프로필 수정페이지에서 프로필을 수정한다.> |
| **1** | <시스템이 프로필 수정페이지에 기존정보를 출력한다.> |
| **2** | <사용자가 수정페이지에서 프로필의 각 항목을 변경하고 저장버튼을 누른다.> |
| **3** | <시스템이 각 항목의 형식이 올바른지 확인한다.> |
| **4** | <시스템이 변경된 항목을 DB에 반영한다.> |
| **5** | <시스템이 변경사항을 즉시 프로필에 반영한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 네트워크나 서버 오류 발생할 경우**<br>…**1a1.**  오류 메시지를 화면에 출력한다.|
| **2** | **2a. 네트워크나 서버 오류 발생할 경우**<br>…**2a1.**  오류 메시지를 화면에 출력한다.|
|  | **2b. 이미지 파일의 형식이 맞지 않는경우**<br>…**2b1.**  “이미지 파일의 형식이 맞지 않습니다”라는 메시지를 출력한다.|
| **3** | **3a. 닉네임의 형식이 올바르지 않은경우**<br>…**3a1.**  "닉네임 형식이 올바르지 않습니다.”라는 메시지를 출력하고 기존 페이지에 머문다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<26> : <이달의 목표 설정>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 독서 통계 페이지에서 이달의 독서 목표를 설정한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <정도희> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원)> |
| **Preconditions** | <사용자가 로그인된 상태여야 한다.<br>시스템이 독서 목표를 DB에 저장하고 있다.> |
| **Trigger** | <사용자가 ‘나의 기록 -> 독서 통계’에서 목표 설정 수정 버튼을 누른 후 팝업에 목표값을 입력하고 완료 버튼을 누른다.> |
| **Success Post Condition** | <시스템은 수정된 목표를 DB에 업데이트하고, 변경사항이 화면에 반영된다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패 등으로 수정이 수행되지 못한 경우 오류 메시지를 원래 값으로 유지된다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 이달의 목표를 설정한다.> |
| **1** | <사용자가 ‘나의 기록 -> 독서 통계’ 페이지에서 나의 목표 설정 버튼을 눌러 목표설정 팝업이 나타난다.> |
| **2** | <목표설정 팝업에 목표값을 입력하고 완료버튼을 누른다.> |
| **3** | <시스템이 변경된 항목을 DB에 반영한다.> |
| **4** | <시스템이 변경사항을 즉시 반영한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. 네트워크나 서버 오류 발생할 경우**<br>…**2a1.**  오류 메시지를 화면에 출력한다.|
|  | **2b. 입력된 값이 정수값이 아닌경우**<br>…**2b1.**  오류 메시지를 화면에 출력하고, 팝업창을 유지한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<27> : <통계 기록 보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 ‘나의 기록’페이지에서 ‘독서 통계’를 선택해서 독서통계(완독도서, 작성한 서평 수, 이번달 독서량, 이번달 목표 독서량, 이번달 목표 달성률, 도서별 평점, 올해의 기록 현황, 자주 읽은 저자)를 확인 할 수 있다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <정도희> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원)> |
| **Preconditions** | <사용자가 로그인된 상태여야 한다.<br>시스템이 독서 통계를 DB에 저장하고 있다.> |
| **Trigger** | <나의 기록에서 독서 통계 항목을 선택한다.> |
| **Success Post Condition** | <아무런 오류가 존재하지 않으면 DB에 저장된 정보를 토대로 독서 통계를 화면에 표시한다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패 통계를 불러오는것이 실패한 경우 오류 메시지를 출력하고 원래 페이지를 유지한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 ‘나의 기록’페이지에서 ‘독서 통계’를 선택해서 독서통계를 확인한다.> |
| **1** | <사용자가 나의 기록에서 독서 통계 항목을 선택한다.> |
| **2** | <시스템이 사용자의 통계 기록을 DB에서 불러온다.> |
| **3** | <시스템이 통계 기록을 사용자에게 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **2** | **2a. DB 및 네트워크 오류가 발생한 경우**<br>…**2a1.**  시스템은 오류 메시지를 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<28> : <자주 읽은 저자 도서 목록 보기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 나의 기록 - 독서 통계에서 자주 읽은 저자의 순위와 그 저자의 읽은 책 목록을 확인할 수 있다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <정도희> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원)> |
| **Preconditions** | <사용자가 로그인된 상태여야 한다.<br>시스템이 사용자의 해당 저자의 독서 기록을 DB로 저장하고 있다.> |
| **Trigger** | <사용자가 독서 통계에서 자주 읽은 저자 목록 중 특정 저자의 펼치기 버튼을 클릭한다.> |
| **Success Post Condition** | <저자이름의 아래로 컨테이너가 확장되어, 도서의 표지와 제목이 나열되어 표시된다.> |
| **Failed Post Condition** | <해당 저자의 책 중 사용자가 읽은 책에 대한 정보가 표시되지 않는다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 독서 통계에서 자주 읽은 저자의 어떤 책을 읽었는지 확인한다.> |
| **1** | <사용자가 ‘나의 기록’ > ‘독서 통계’ > ‘자주 읽은 저자의 순위’에서 특정 저자의 펼치기 버튼을 클릭한다.> |
| **2** | <시스템이 DB에서 사용자가 읽은 해당 저자의 책의 목록, 표지, 제목을 불러온다.> |
| **3** | <시스템이 불러온 책의 목록, 표지, 제목을 표시한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 네트워크나 서버 오류 발생할 경우**<br>…**1a1.**  오류 메시지를 화면에 출력한다.|
| **2** | **2a. 네트워크나 서버 오류 발생할 경우**<br>…**2a1.**  오류 메시지를 화면에 출력한다.|
| **3** | **3a. 3a. 접기 버튼을 클릭하면 **<br>…**3a1.**  확장되었던 컨테이너가 줄어들며 다시 책 목록을 가린다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<29> : <알림 확인>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <사용자가 알림창에서 자신에게 온 알림을 확인한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <User Level> |
| **Author** | <정도희> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <User (회원)> |
| **Preconditions** | <사용자가 로그인된 상태여야 한다.<br>시스템이 알림을 DB로 저장하고 있다.> |
| **Trigger** | <사용자가 알림 아이콘을 눌러서 알림을 확인한다.> |
| **Success Post Condition** | <알림이 존재하지않으면 알림이 없다는 표시를 한다.<br>알림이 존재하면 알림을 표시한다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패 등으로 알림을 불러오는것을 실패한 경우 오류메시지 출력한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <사용자가 알림을 확인한다.> |
| **1** | <사용자가 알림 아이콘을 눌렀을 때 실행된다.> |
| **2** | <시스템이 DB에서 알림을 불러와서 알림창에 띄운다.> |
| **3** | <시스템이 사용자에게 알림을 알림창에 표시한다.> |
| **4** | <시스템이 DB에 알림이 확인되었음을 갱신한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 네트워크나 서버 오류 발생할 경우**<br>…**1a1.**  오류 메시지를 화면에 출력한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 1 seconds|
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---

# Use Case #<30> : < 책 정보 불러오기>

## GENERAL CHARACTERISTICS

| 항목 | 내용 |
|---|---|
| **Summary** | <책의 정보를 도서 정보 시스템(naver 도서 api)에서 불러와서 DB에 저장한다.> |
| **Scope** | <CHAECK system> |
| **Level** | <ExternalBookApiClient Level> |
| **Author** | <정도희> |
| **Last Update** | <2025.10.27> |
| **Status** | <Draft> |
| **Primary Actor** | <도서정보시스템(naver 도서 api)> |
| **Preconditions** | <도서정보시스템에 해당도서의 데이터가 등록되어 있어야 한다.> |
| **Trigger** | <사용자가 검색창에 책을 검색했을 때 DB에 해당 책의 정보가 저장되어있지 않을 때.> |
| **Success Post Condition** | <도서 정보 시스템에 해당책의 데이터가 존재할 경우, 정보를 불러와서 DB에 저장한다.<br>도서 정보 시스템에 해당책의 데이터가 존재하지 않을 경우, 정보를 불러오지않고 아무런 메시지를 출력하지 않는다.> |
| **Failed Post Condition** | <서버 오류나 DB 접근 실패, api호출이 실패했을 때, 검색 자체가 수행되지 못한 경우 로그에 에러메시지를 출력한다.> |

## MAIN SUCCESS SCENARIO

| Step | Action |
|---|---|
| **S** | <도서 정보 시스템이 도서 정보를 불러와서 DB에 저장한다.> |
| **1** | <이 usecase 는 사용자가 검색창에 책을 검색했을 때 DB에 해당 책의 정보가 저장되어있지 않을 때 수행된다.> |
| **2** | <시스템이 책검색 키워드로 도서정보시스템의 api를 호출한다.> |
| **3** | <시스템이 api호출결과를 DB에 저장한다.> |

## EXTENSION SCENARIO

| Step | Branching Action |
|---|---|
| **1** | **1a. 네트워크나 서버 오류 발생할 경우**<br>…**1a1.**  오류 메시지를 화면에 출력한다.|
| **2** | **2a. 일치하는 책이 없을 경우**<br>…**2a1.**  아무런 행동도 취하지 않는다.|
|  | **2b. api호출에 실패 했을 경우**<br>…**2b1.**  로그에 호출이 실패하였음을 기록한다.|
| **3** | **3a. DB오류가 발생한 경우**<br>…**3a1.**  로그에 DB오류가 났음을 기록한다.|

## RELATED INFORMATION

| 항목 | 내용 |
|---|---|
| **Performance** | <= 2 seconds|
| **Frequency** | 상시 사용 |
| **Concurrency** | 제한 없음 |
| **Due Date** | <2025.11.07> |

---
