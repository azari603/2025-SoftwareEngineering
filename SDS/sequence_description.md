|#seq7|`계정 탈퇴`|
|---|:--------------------------:|
|_이미지_|<img width="548" height="513" alt="image" src="https://github.com/user-attachments/assets/74109fbc-7993-423e-90ae-c77c9b96e800" />|
|_설명_|사용자가 계정을 탈퇴하는 과정을 나타내는 sequence diagram이다. 사용자가 계정 탈퇴를 요청하면 UserController가 UserService에 탈퇴 요청을하고 UserService가 ProfileRepository에서 현재 계정의 사용자 ID를 받아오고 이 ID의 상태를 DELETED로 저장한다. 이 과정을 모두 성공하면 UserService에서 UserController에게 success라는 응답을 주고 UserController가 User에게 계정 탈퇴가 완료됐다는 메시지를 출력한다. 최종적으로 계정은 탈퇴된 상태가 되고, User는 탈퇴여부를 알 수 있다. |

|#seq8|`책 검색`|
|---|:--------------------------:|
|_이미지|<img width="1508" height="1111" alt="image" src="https://github.com/user-attachments/assets/b667195f-48fc-4927-86b8-1219d1eef5e0" />|
|_설명_|사용자가 도서를 검색하는 기능을 나타내는 sequence diagram이다. 사용자가 검색창에 키워드를 입력하고 검색을 요청하면, SearchController는 해당 요청을 하고 BookService에 전달한다. BookService는 우선 BookRepository를 호출하여 입력된 키워드에 해당하는 도서가 DB에 존재하는지 확인한다.DB에서 검색 결과가 존재할 경우, BookRepository는 해당 도서 정보를 BookService에 반환하고, BookService는 이를 Page<BookCardResponse> 형태로 SearchController를 거쳐 사용자에게 검색 결과를 보여준다.DB에 검색 결과가 존재하지 않을 경우, BookService는 ExternalBookClient를 통해 외부 도서 API로 검색 요청을 보낸다. 외부 API에서 ExternalBookDto 리스트 형태로 결과를 받으면, BookService는 이를 BookIngestOecherstrator로 전달하여 DB에 저장하도록 요청한다.저장 과정이 완료되면 BookService는 다시 BookRepository를 호출하여 새로 저장된 도서 게이터를 조회하고, 조회된 결과를 Page<BookCardResponse> 형태로 SearchController에 전달한다. 최종적으로 SearchController는 사용자에게 새로 추가된 도서를 포함한 검색 결과 화면을 출력한다. |

|#seq9|`책 정보 상세정보`|
|---|:--------------------------:|
|_이미지_|<img width="1262" height="1111" alt="image" src="https://github.com/user-attachments/assets/c196a42c-c704-4b8b-96bc-6099890b78d7" />|
|_설명_| 사용자가 도서의 상세 정보를 조회하는 과정을 나타낸다. 사용자가 특정 도서의 bookId로 상세 페이지를 요청하면, BookController는 해당 요청을 받아 BookService에 도서 정보를 조회하도록 전달한다. BookService는 먼저 BookRepository를 통해 내부 데이터베이스에 해당 도서가 존재하는지 확인한다.만약 도서 정보가 DB에 존재한다면, BookService는 해당 도서 엔티티를 BookDetailDto로 변환하여 BookController에 반환하고, 이후 사용자에게 도서 상세 정보를 화면에 표시한다.도서 정보가 DB에 존재하지 않을 경우, BookService는 ExternalBookClient를 호출하여 외부 도서 API로부터 해당 ISBN의 책 정보를 조회한다. 조회된 외부 도서 정보는 BookIngestOrchestrator를 통해 내부 엔티티로 변환되어 BookRepository에 저장된다. 저장이 완료되면 BookService는 다시 BookRepository를 호출하여 새로 추가된 책 정보를 조회하고, 이를 BookDetailDto로 변환하여 BookController에 반환하고 이후 최종적으로 사용자에게 새로 저장된 도서의 상세정보를 화면에 표시한다.|

|#seq10|`추천 책 조회`|
|---|:--------------------------:|
|_이미지_|<img width="1141" height="1221" alt="image" src="https://github.com/user-attachments/assets/51c63489-ed8c-4c0f-acab-04755a2e56b1" />|
|_설명_|사용자가 메인 화면에 접속했을 때 로그인 여부에 따라 자동으로 서로 다른 추천 도서 목록이 표시되는 과정을 나타낸 sequence diagram이다. 사용자가 메인 화면에 접속하면, RecommendationController가 자동으로 호출되어 RecommnedationService에 도서 추천 요청을 전달한다.로그인한 사용자의 경우, RecommendationService는 recommendForUser(userId, pageable) 메서드를 실행하여 현재 사용자의 독서 활동과 리뷰 데이터를 기반으로 개인 맞춤형 추천을 생성한다. 이를 위해 내부의 RecommendationEngine이 작동하며, ReadingStatusRepository, ReviewRepository, ReviewLikeRepository를 조회하여 사용자의 선호도를 분석한다. 분석 결과를 통해 RecommendationEngine은 추천 도서의 ID 목록을 생성하고 이를 RecommendationService에 반환한다. RecommendationServie는 ID 목록을 이용해 BookRepository를 호출하고, findByIdIn(bookIds, pageable) 메서드로 DB에서 해당 도서의 상세 정보를 조회한다. 조회된 도서 정보는 Page<BookCardDto> 형태로 변환되어 컨트롤러로 전달되며, RecommendationController는 이를 메인화면에 표시한다.로그인하지 않은 사용자의 경우 RecommendationService는 fallbackPopular(pageable) 메서드를 호출하여 BookRepository에서 인기 순으로 정렬된 도서 목록을 조회한다. BookRepository는 findTopByOrderByPopularityDesc(pageable) 쿼리를 실행하여 현재 시스템 내에서 가장 인기 있는 도서들을 반환한다. 해당 결과는 Page<BookCardDto> 형태로 컨트롤러에 전달되며, 비로그인 사용자의 메인 화면에는 인기 도서 목록이 표시된다. |

|#seq11|`책BTI`|
|---|:--------------------------:|
|_이미지_<img width="1684" height="1111" alt="image" src="https://github.com/user-attachments/assets/bc00f999-91c0-45e5-b2cb-7896d88be19d" />|
|_설명_|사용자가 BookBTI 테스트를 수행하고 결과와 함께 맞춤형 추천 도서가 자동으로 표시되는 과정을 나타내는 sequence diagram이다.사용자가 테스트를 시작하면 BookBtiController가 BookBtService를 호출하여 세션을 초기화하고, BookBtiService는 BtiQuestionRepository에서 질문 목록을 조회해 사용자에게 표시한다. 사용자가 각 질문에 답변할 때마다 BookBtiService는 세션 상태를 갱신하고 다음 질문을 반환한다.모든 답변이 완료되면 finish()가 호출되어 BookBtiService는 BtiQuestionRepository에서 문항을 불러와 calculateResult()로 결과를 계산하고, BtiResultRepository에 저장한다.이후 RecommendationService를 호출해 recommendForUser()를 실행하고, RecommendationEngine이 사용자의 활동 데이터를 분석해 추천 도서 ID 목록을 생성한다. BookRepository는 해당 도서 정보를 조회해 Page<BookCardDto> 형태로 반환한다. |

|#seq12|`도서 상태 지정`|
|---|:--------------------------:|
|_이미지_|<img width="1158" height="1111" alt="image" src="https://github.com/user-attachments/assets/f0b8c92e-8905-4a36-8298-e477d46552f5" />|
|_설명_| 사용자가 특정 도서 읽기 상태를 지정하거나 변경하는 과정을 나타내는 sequence diagram이다. 사용자가 특정 도서의 읽기 상태를 선택하면 ReadingStatusController가 요청을 받아 ReadingStatusService의 setStatus() 메서드를 호출한다. ReadingStatusService는 먼저 UserRepository와 BookRepository를 통해 요청에 포함된 사용자의 ID와 도서 ID가 유효한지 확인한다. 이후 ReadingStatusRepository의 findUserIdAndBookId()를 호출하여 해당 사용자의 기존 읽기 상태가 존재하는지 조회한다.기존 상태가 없을 경우, 새로운 ReadingStatus 객체를 생성하여 save() 메서드로 저장한다.기존 상태가 이미 존재할 경우, 사용자의 동일한 상태를 다시 선택한 경우, 기존 상태를 삭제한다.기존 상태와 다른 상태를 선택한 경우, 기존 엔티티를 수정하여 새로운 상태로 갱신 후 save() 한다.모든 처리가 정상적으로 완료되면 ReadingStatusServicesms 성공 응답을 반환하고 사용자에게 상태 변경 완료된 화면을 표시한다.|

|#seq13|`지정 상태별로 책 조회`|
|---|:--------------------------:|
|_이미지_|<img width="1251" height="642" alt="image" src="https://github.com/user-attachments/assets/0cae5145-73e5-4a2f-b95b-650d1926c60b" />|
|_설명_| 로그인한 사용자가 ‘나의 기록 -> 나의 서재’ 페이지에 접속하여 특정 읽기 상태를 선택하였을 때 해당 상태의 도서 목록을 불러오는 과정을 나타내는 sequence diagram이다.사용자가 페이지에 접속하면 화면 상단에는 상태 태그가 표시되고, 사용자가 특정 태그를 클릭하면 ReadingStatusController의 listByStatus(status, page, size) 메서드가 호출되어 해당 상태에 속한 도서 목록 조회 요청이 발생한다. 컨트롤러는 ReadingStatusService의 getByStatis(userId, status, pageable) 메서드를 호출하며, 서비스는 ReadingStatusRepository의 findByUserAndStatus()를 통해 현재 로그인한 사용자와 선택된 상태에 해당하는 ReadingStatus 목록을 조회한다. 해당 결과는 Page<ReadingStatus> 형태로 반환된다.ReadingStatusService는 조회된 각 ReadingStatus에 연결된 bookId를 기준으로 BookRepository의 findById(bookId) 메서드를 반복 호출하여 각 도서의 세부 정보를 불러온다. 조회된 책 정보들은 ReadingStatusDto로 매핑되어 상태별 도서 목록으로 변환된다.|

|#seq14|`서평 작성`|
|---|:--------------------------:|
|_이미지_|<img width="988" height="601" alt="image" src="https://github.com/user-attachments/assets/b12e2ecb-7c2b-4871-831c-ee52161a960f" />|
|_설명_| 사용자가 서평을 작성하는 과정을 나타내는 sequence diagram이다. 사용자가 서평 작성을 요청하면 ReviewController를 통해서 서평을 작성을 요청한다. 이후 ReviewService에 서평 작성을 만든다. 이때 ReviewService가 UserRepository에서 사용자의 ID를 받아온다. 하지만 현재 사용자의 ID가 UserRepository에 존재하지 않는 경우 로그인이 안된 사용자로 인식하여 로그인 페이지로 넘어간다. 이후 BookRepository를 통해 책의 정보를 받아온다. 서평 작성 이후 저장을 하면 사용자의 ID, 책의 정보, 서평의 상세내용이  ReviewRepository에 저장되고 create에 대한 리턴값을 받고 서평이 작성된다.|

|#seq15|`다른 사람 서평 조회`|
|---|:--------------------------:|
|_이미지_|<img width="1043" height="701" alt="image" src="https://github.com/user-attachments/assets/9a7f0ca3-7a25-4b45-b097-d647fb6bdd04" />|
|_설명_|사용자가 다른 사람의 서평을 조회할 때 과정을 나타내는 sequence diagram이다. 사용자가 ReviewController를 통해 조회하고 싶은 서평을 요청하면 ReviewService에서 ReviewRepositroy에 있는 서평 정보를 가져온다. 이 정보에는 작성자,좋아요 수, 댓글, 팔로우 여부가 포함되어 있어야하므로 UserRepository,ReviewLikeRepository,CommentRepository,FollowRepositroy에서 각각 정보를 가져와 ReviewService를 통해 사용자에게 보여진다. 이를통해 사용자는 해당 서평의 상세정보를 모두 확인할 수 있다.  |

|#seq16|`서평 상호작용`|
|---|:--------------------------:|
|_이미지_|<img width="1062" height="752" alt="image" src="https://github.com/user-attachments/assets/5f76e251-5def-4fde-9407-8b80accfb712" />|
|_설명_|사용자가 다른 사용자의 서평을 조회하면서 좋아요,댓글과 같은 상호작용을 할 때의 과정을 나타내는 sequence diagram이다.사용자가 좋아요 버튼을 누를 경우, ReiviewInteratctionController에서 ReviewService로 좋아요를 눌렀다는 정보를 전달한다. 이 정보는 ReviewRepository와 ReviewLikeRepository를 거쳐 해당 서평에 대한 좋아요 정보를 받아오는데 사용된다. 이후 좋아요 정보에서 좋아요를 누르지 않은 상태라면 좋아요를 추가로 저장하고, 좋아요를 이미 누른 상태라면 좋아요를 삭제한다. 이후 사용자에게 변경이 반영된 좋아요 수를 표시한다. 또한 댓글을 달 경우 Reviewservice에서 CommentRepository에 댓글을 저장하고 유저에게 댓글이 작성된 이후의 댓글창을 표시한다. 댓글을 삭제하는 경우에는 CommentId(cid)를 통해 Comment Repository에서 해당 댓글을 찾아내 삭제한 이후 사용자에게는 삭제된 이후의 댓글 창을 표시한다.  |

|#seq17|`좋아요한 서평 목록 보기'|
|---|:--------------------------:|
|_이미지_|<img width="821" height="742" alt="image" src="https://github.com/user-attachments/assets/9b03cef6-4cec-4cf9-ae68-1c4cf3f083ef" />|
|_설명_|사용자가 좋아요 표시한 서평 목록을 조회하는 과정을 나타내는 sequence diagram이다. 사용자가 해당 페이지를 요청하면 StatsController에서 ReviewService에 사용자의 아이디를 주고 좋아요한 서평 표시를 요청한다. ReviewService에서는 해당 아이디를 통해 UserRepository에서 사용자의 정보를 알아내고 이를 이용해 ReviewLikeRepository에서 해당 사용자의 좋아요한 서평의 정보를 받아온다. 만약 좋아요한 서평이 존재 하지 않는 경우 빈 카드를 리턴하여 빈 목록을 표시하고, 존재한다면 ReviewRepository에서 서평 정보를 받아와 사용자에게 목록을 표시한다. |

|#seq18|`나의 서평 목록 조회`|
|---|:--------------------------:|
|_이미지_|<img width="761" height="451" alt="image" src="https://github.com/user-attachments/assets/0f7fbecd-0b01-44dd-82e4-d5750760bf12" />|
|_설명_|사용자가 자신이 작성한 서평 목록을 조회하는 과정이다. 사용자가 서평 목록을 조회하기를 요청하면 ReviewController의 listMine을 호출한다. Controller는 로그인된 사용자의 id와 페이지 정보를 이용해 ReviewService의 getMyReviews를 호출한다. 그 후 ReviewRepository의 findByUserIdAndDelete 메서드를 실행하여 삭제되지 않은 사용자의 서평만을 조회한다.  |

|#seq19|`나의 서평 상세 보기`|
|---|:--------------------------:|
|_이미지_|<img width="687" height="451" alt="image" src="https://github.com/user-attachments/assets/e674565f-e005-4b76-9779-20115180ce5d" />|
|_설명_|사용자가 자신이 작성한 서평을 상세 보기하는 과정이다. 상세 조회 요청에 따라 ReviewController로 서평 Id(reviewId)가 전달되며, ReviewService로 사용자Id(viewerId)와 서평 Id가 전달된다. Service 계층은 ReviewRepository를 통해 서평 데이터를 조회하는 동시에, 전달받은 viewerId와 서평의 작성자 Id를 비교하여 소유 여부를 판단한다. 최종적으로 Service는 조회된 데이터와 소유 여부 판단 결과를 결합한 ReviewDetailDto를 controller에 반환하며 응답이 사용자에게 전달한다. |

|#seq20|`나의 서평 수정`|
|---|:--------------------------:|
|_이미지_|<img width="682" height="731" alt="image" src="https://github.com/user-attachments/assets/ad351c5f-39c0-4f41-af4e-7795aab35232" />|
|_설명_|사용자가 자신이 작성한 서평을 수정하는 과정이다. 수정 요청에 따라 서평Id(reviewId)와 수정 데이터(req)가 ReviewController로 전달되며, Controller는 인증 정보를 통해 사용자 Id(userId)를 추출한 뒤 이를 함께 ReviewService에 전달한다. Service 계층은 ReviewRepository의 findById를 호출하여 서평 데이터를 조회하고, 조회된 서평의 작성자 Id와 현재 사용자 Id를 비교해 수정 권한을 검증한다. 사용자가 서평의 작성자일 경우 서평의 내용을 갱신하고, save(review)로 저장한다. 이후 수정된 서평의 최신 정보를 다시 조회하여 사용자에게 전달한다. |

|#seq21|`나의 서평 삭제`|
|---|:--------------------------:|
|_이미지_|<img width="685" height="631" alt="image" src="https://github.com/user-attachments/assets/159095e2-a7d2-47c8-9964-f5c00a055085" />|
|_설명_| 사용자가 자신이 작성한 서평을 삭제하는 과정이다. 삭제 요청이 발생하면 서평ID(reviewId)가 ReviewController로 전달되고, Controller는 사용자ID(userId)를 추출한 뒤 이를 함께 ReviewService에 전달한다. Service 계층은 ReviewRepository의 findById(reviewId)를 호출하여 해당 서평 데이터를 조회하고, 조회된 서평의 작성자Id와 현재 사용자Id를 비교한다. 두 ID가 일치할 경우softDelete(reviewId)를 실행하여 서평을 논리적으로 삭제하고 저장을 완료한다. 모든 처리가 완료된 후 Controller는 삭제 완료 메시지를 생성하여 사용자에게 응답을 반환 한다.|

|#seq22|`서평 둘러보기`|
|---|:--------------------------:|
|_이미지_|<img width="1205" height="1161" alt="image" src="https://github.com/user-attachments/assets/2aeb032c-6c63-4dce-a0e4-4db1bcbdf79e" />|
|_설명_|  사용자가 지정한 타입의 서평 둘러보기 기능을 사용하는 과정을 나타내는 sequence diagram이다. 처음에 사용자가 최신 피드 혹은 팔로잉 피드를 고른다. 최신피드의 경우 FeedService에게 최신 피드를 가져오도록 명령하고, 이에 맞춰서 FeedService는 ReviewRepository에서는 서평 내용, ReviewLikeRepository에서는 서평의 좋아요 수, CommentRepository에서는 서평의 댓글 수를 가지고 온다. 가져온 정보들을 FeedController로 가져가 사용자에게 보여준다.만약 팔로우 피드를 고른다면 FeedService는 우선 FollowService에서 팔로우 목록을 가져오고, ReviewRepository에서 팔로우한 사용자의 서평만 가져오도록 한다. 이후에는 서평 내용,좋아요 수, 댓글 수를 불러와서 표시하도록한다. |

|#seq23|`팔로우/팔로잉 취소`|
|---|:--------------------------:|
|_이미지_|<img width="832" height="951" alt="image" src="https://github.com/user-attachments/assets/d15589db-74e2-4b69-8ef5-ae29ba3325a1" />|
|_설명_|사용자가 다른 사용자를 팔로우하거나 팔로우를 취소하는 과정이다. 사용자가 대상 사용자(targetUser)의 팔로우 버튼을 클릭하면 FollowController는 현재 사용자의 ID(followerId)와 대상 사용자의 ID(followeeId)를 전달받아 FolloweService의 isFollowing 메서드를 호출한다. FollowRepository의 findByFollowerIdAndFolloweeId를 통해 두 사용자 간의 팔로우 관계 존재 여부를 확인하고 그 결과를 Controller에 반환한다. Controller는 이를 기반으로 현재 상태가 팔로우 중인지 아닌지를 판단하고, 조건에 따라 분기한다. 팔로우 관계가  이미 존재할 경우, unfollow 요청이 발생하며 Service는 FollowRepository의 deleteById를 호출해 해당 팔로우 관계를 삭제한다. 반대로 팔로우 관계가 존재하지 않을 경우, follow 요청이 실행되어, Service가 FollowRepository를 통해 새로운 팔로우 관계를 생성한다. 모든 처리가 완료된 후 Controller는 각각의 결과에 따라 팔로우 성공 또는 팔로우 취소 응답을 사용자에게 반환한다. |

|#seq24|`팔로잉/팔로워 목록 조회`|
|---|:--------------------------:|
|_이미지_|<img width="758" height="772" alt="image" src="https://github.com/user-attachments/assets/f4fa592b-df48-4a88-9060-242859012269" />|
|_설명_|사용자가 자신의 팔로잉 또는 팔로워 목록을 조회하는 과정이다. 사용자가 프로필 화면에서 팔로잉 탭을 클릭하면 FollowController는 현재 사용자ID(userId)와 페이지 정보를 전달받아, FollowService의 getFollowings를 호출한다. Service 계층은 FollowRepository의 findFolloweeIdsByFollowerIdAndStatus 메서드를 통해 사용자가 팔로우하고 있는 대상 사용자들의 ID 목록을 조회하고, 이를 Controller에 반환한다. Controller는 이 데이터를 사용자에게 전달하여 팔로잉 목록을 출력한다.  사용자가 팔로워 탭을 클릭하면 FollowController는 동일한 방식으로 getFollowers 메서드를 호출하며 findFollowerIdsByFolloweeIdAndStatus를 이용해 해당 사용자를 팔로우하는 사용자들의 ID 목록을 조회한다 이후 결과를 사용자에게 전달하여 팔로워 목록을 표시한다. |

|#seq25|`프로필 수정`|
|---|:--------------------------:|
|_이미지_|<img width="611" height="1602" alt="image" src="https://github.com/user-attachments/assets/53ce1492-a6ee-4ffb-bc58-1a46395c56d0" /> |
|_설명_|  사용자가 프로필 설정 화면에서 닉네임이나 소개를 바꾸고 저장을 누르면, 시스템은 먼저 DB에서 현재 프로필을 읽어 와서 새 값이 규칙에 맞는지 점검한다. 금지어, 길이, 포맷이 어긋나면 저장을 멈추고 화면에 입력 형식 오류를 보여 준다. 문제가 없으면 바뀐 내용을 프로필에 반영해 DB에 기록하고, 성공으로 응답하면서 화면에는 갱신된 값이 그대로 반영된다.프로필 이미지를 바꿀 때는 사용자가 올린 파일의 유형, 크기, 해상도를 먼저 검사한다. 규격을 벗어나면 즉시 이미지 형식 오류를 알려 주고 업로드를 중단한다. 검사를 통과하면 이미지 보관소에 파일을 업로드하고 공개 주소를 만든 뒤, 그 주소를 프로필에 연결해 DB에 저장한다. 저장이 끝나면 새 이미지 주소가 담긴 성공 응답이 돌아와 화면에 바로 교체된 이미지가 보인다.배경 이미지를 바꿀 때도 흐름은 동일하다. 파일 검사를 거쳐 문제가 없으면 업로드와 주소 발급을 진행하고, 해당 주소를 프로필의 배경으로 연결해 저장한다. 이후 성공 응답과 함께 화면에서 배경이 즉시 갱신된다.DB나 저장소 접근 중 오류가 나면 해당 단계에서 중단하고 서버 오류로 응답하며, 화면은 실패 안내를 보여 주고 기존 상태를 유지한다. |

|#seq26|`이달의 목표 설정`|
|---|:--------------------------:|
|_이미지_|<img width="549" height="461" alt="image" src="https://github.com/user-attachments/assets/29b2fd59-d84b-464b-9ee1-fa430ff217c2" />|
|_설명_|  사용자가 ‘나의 기록 → 독서 통계’에서 목표 설정 버튼을 눌러 값을 입력하고 완료를 누르면, 시스템은 우선 입력이 정수인지와 허용 범위인지(0 이상) 확인한다. 값이 0이하면 이번 달 목표를 해제하는 의미로 해석하고, 양수면 해당 값으로 목표를 잡는다. 검증을 통과하면 시스템은 DB에서 사용자의 프로필을 읽어 온 뒤, 목표 필드를 새 값(또는 해제)으로 바꾸고 저장한다. 저장이 끝나면 화면에는 별도 본문 없이 성공으로 응답하고(내용 없이 완료), 통계 위젯의 목표 및 달성률이 즉시 갱신된다. 입력이 정수가 아니거나 허용 범위를 벗어나면 저장을 진행하지 않고 팝업에 입력 오류 안내를 띄운다. DB 접근 중 문제가 생기면 저장을 중단하고 서버 오류를 알리며, 화면의 목표 값은 이전 상태를 유지한다.|

|#seq27|`통계 기록 보기`|
|---|:--------------------------:|
|_이미지_|<img width="961" height="1462" alt="image" src="https://github.com/user-attachments/assets/27fc7fce-994d-4aa8-8d61-50f98c7bb767" />|
|_설명_|  사용자가 ‘나의 기록’에서 ‘독서 통계’를 열면, 화면은 먼저 개요·별점 히스토그램·타임라인·자주 읽은 저자·카테고리 분포·이달의 목표/진척을 순차(또는 병렬)로 요청한다. 시스템은 로그인 사용자를 기준으로 DB에서 본인이 작성한 서평과 완독 상태 기록을 읽어 온다. 개요와 카테고리 분포, 자주 읽은 저자는 서평 목록을 집계해 총 읽은 권수, 평균 별점, 별점 분포, 저자 상위 랭킹, 카테고리 비율을 계산해 돌려준다. 별점 히스토그램은 서평들의 평점을 그대로 세어 막대 데이터로 만든다. 타임라인은 완독 상태 기록을 기간별로 묶어 월(또는 연) 단위 카운트를 만들어 전달한다. 이달의 목표/진척은 사용자의 프로필에서 목표 값을 확인하고, 이번 달 완독 수를 더해 달성률을 계산해 보여준다. 어떤 단계에서든 DB 접근이나 네트워크 문제가 발생하면 해당 카드 대신 오류 메시지를 띄우고, 나머지 카드들은 가능한 범위에서 정상 데이터를 계속 보여준다.|

|#seq28|`자주 읽은 저자 도서 목록 보기`|
|---|:--------------------------:|
|_이미지_|<img width="1011" height="1021" alt="image" src="https://github.com/user-attachments/assets/7c3d0796-ed79-4c51-a639-3f740f170e8a" />|
|_설명_|   사용자가 ‘나의 기록 → 독서 통계’에서 자주 읽은 저자 목록의 펼치기 버튼을 누르면, 화면은 선택된 저자를 기준으로 내가 완독한 기록만 모으기 위해 먼저 독서 상태 저장소에서 해당 사용자의 완독(Completed) 기록을 읽어 온다. 이때 완독 목록 안에서 저자 기준으로 묶어 빈도 상위를 계산하고, 사용자가 펼친 저자에 대해서는 그 저자의 책들 중 내가 완독한 항목들만 추려 낸다. 추려진 각 책에 대해서는 도서 저장소에서 표지 이미지와 제목 등 메타 정보를 가져와 카드 목록을 만든다. 이렇게 합쳐진 결과가 컨트롤러로 전달되고, 컨트롤러는 확장된 컨테이너에 표지와 제목이 나열된 리스트를 그려 준다. 완독 기록 안에 해당 저자의 책이 하나도 없으면, 확장 컨테이너는 비어 있는 상태로 표시되고 별도 경고는 띄우지 않는다. DB 접근이나 네트워크 오류가 발생하면 목록 생성을 중단하고 오류 안내만 보여 주며, 기존 화면 상태는 유지된다.|

|#seq29|`알림 확인`|
|---|:--------------------------:|
|_이미지_|<img width="667" height="1841" alt="image" src="https://github.com/user-attachments/assets/1f6c15bb-f635-41d0-b239-81544d783229" />|
|_설명_| 알림 아이콘을 누르면 화면은 먼저 내 알림을 시간 내림차순으로 가져와 목록을 그린다. 동시에 읽지 않은 알림의 개수를 세어 배지에 표시한다. 목록이 열려 있는 동안 내가 특정 알림 카드를 눌러 상세로 이동하거나 확인 동작을 하면, 그 알림을 읽음으로 바꿔 다시 저장한다. 필요하면 카드의 메뉴에서 삭제를 선택해 해당 알림을 지울 수 있고, 목록은 즉시 갱신된다.DB 조회나 저장 과정에서 문제가 생기면 목록·배지 대신 오류 안내가 표시되고, 읽음 처리나 삭제는 수행되지 않는다.|

|#seq30|`책 정보 불러오기`|
|---|:--------------------------:|
|_이미지_|<img width="949" height="822" alt="image" src="https://github.com/user-attachments/assets/c9665a08-ef3b-4895-8aa8-f3ce39b77972" />|
|_설명_|  사용자가 제목이나 저자 키워드를 넣어 책을 찾으면, 시스템은 먼저 내부 DB에서 해당 키워드에 맞는 책 카드들을 검색한다. 이미 저장된 결과가 있으면 그대로 목록을 만들어 돌려준다. 저장된 결과가 하나도 없으면, 시스템은 외부 도서 API에 같은 키워드로 검색을 요청하고, 받아온 책 메타를 내부 형식으로 변환해 DB에 차례로 저장한다. 인입이 끝나면 방금 저장된 데이터 기준으로 다시 한 번 DB를 조회해 책 카드 목록을 구성해준다.외부 API 호출이 실패하면 목록 생성은 중단되고 “외부 API 오류”로 응답한다. 호출은 성공했지만 결과가 없으면, 저장 없이 빈 목록을 그대로 반환한다.|
