
# coincloud server


### 운영서버
```
ssh ec2-user@13.125.73.226 -i ~/aws-pem/gncloud-io.pem
```

### 텔레그램봇

##### 운영용
- ko_coincloud_bot : `554326047:AAFNFlxO2v0UqEast3ekAQZgMc6b3xE0P0I`

##### 개발용
- ko_coincloud_01_bot : `525111213:AAGTLRbw2dJKOUXo9K1QPRnCMHwYtk1sjg0`
- ko_coincloud_02_bot : `574061087:AAFj0wjWMeGJPyI8kX5TinYpFf0i8cHNsV0`

### 실행옵션

##### SPRING_OPT
- 텔레그램봇 활성화 : `--spring.profiles.active=telegram`
- 로그 외부설정 : `--logging.pattern.file=$MGB_HOME/conf/logback-spring.xml`
- application.yaml : `--spring.config.location=file:`


### REST API 
##### URL http://loclahost:8080/v1


- ## 알고리즘 소스 조회
	- #### 경로
		- /algos/{id} (GET)
	- #### 파라미터 
	- 없음

- ## 알고리즘 전체 조회
	- #### 경로
		- /algos/me (GET)
	- #### 해더
		- X-coincloud-user-id: token

- ## 알고리즘 생성
	- #### 경로
		- /algos (POST)
	- #### 파라미터
		- userId
		- code
 
- ## 알고리즘 수정
	- #### 경로
		- /algos/{id} (PUT)
	- #### 파라미터 
		- code
 
- ## 알고리즘 실행
	- #### 경로
		- /tasks (POST)
	- #### 파라미터 
		- task
		    - id
            - exchangeName
            - baseCurrency
            - capitalBase
            - live
            - simulationOrder
            - startTime
            - endTime
            - dataFrequency
        - exchangeAuths [array type]
            - exchange
            - key
            - secret

# example
- ### 알고리즘 생성
```
curl -X POST \
  http://localhost:8080/v1/algos \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 1e44f916-f90d-3a0f-8e0d-d07d193f8998' \
  -d '{
	"userId":"joonwoo",
	"code":"[알고리즘 코드]"
}'
```
- ### 알고리즘 전체 조회
```
curl -X GET \
  curl -X GET \
    http://localhost:8080/v1/algos/me \
    -H 'cache-control: no-cache' \
    -H 'postman-token: 92662a87-5afd-ef84-e524-33ff6910d019' \
    -H 'x-coincloud-user-id: 2c359971-42fe-4989-afd6-97ae03a916c8'
```
- ### 알고리즘 시작
```
curl -X POST \
  http://api.gncloud.io:8080/v1/tasks \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 4ac09a3f-e265-5618-dfe4-fc8f5ac7c6c2' \
  -d '{
	"task":{
		"id":"a0b95b56-f015-4785-b950-917c12497afa",
		"exchangeName":"poloniex",
		"baseCurrency":"btc",
		"capitalBase":"10000",
		"live":"true",
		"simulationOrder":"false"
	},
	"exchangeAuths":[
		{
			"exchange":"bittrex",
			"key":"a2",
			"secret":"a3"
		}
	]
}
'
```


## 로그인 및 회원가입 API

### 1. 회원가입

[요청]
```
POST http://127.0.0.1:8080/auth/signUp
{
	"userId": "swsong5",
	"email": "songaal@gmail.com"
}
```

[결과]
```
{
    "username": "swsong5",
    "attributes": [
        {
            "name": "sub",
            "value": "08f6535d-35a4-4ddf-9ccd-7addfa33786b"
        },
        {
            "name": "email",
            "value": "songaal@gmail.com"
        }
    ],
    "userCreateDate": 1523422093514,
    "userLastModifiedDate": 1523422093514,
    "enabled": true,
    "userStatus": "FORCE_CHANGE_PASSWORD",
    "mfaoptions": null
}
```

** 이메일을 통해 임시비번을 확인한다

```
제목 : 코인클라우드 환영메시지

코인클라우드 가입을 환영합니다. 

사용자 이름은 songaal이고 임시 암호는 2X3VD.입니다. 

초기 로그인후 비밀번호를 변경할 수 있습니다. 

```


### 2. 임시비번 로그인
[요청]
```
POST http://127.0.0.1:8080/auth/login
{
	"userId": "songaal",
	"password": "2X3VD."
}
```

[결과]
```
{
    "session": "Fsof-k3FDfxFajErZAHeOwpmZXl66l1quUpVOQ30oSwAPuH63gbMO6sxqr8N4Okv64a5p3WR0U-46EWxxTJt2ogUItBt-pn6RQPcGlY19t-lp6r7_D_hwh8Jmvts0grWmBh82Gvy5mJgSkewgaUkS3xctDZNTkSgACFHJRMnGpUVSI3yltl51vLWoWboILq8uwE2ANVfnjHdyLIkiUFpYT-CfFQ_q9ZCj2dMnPAfXPh9fz8J6xFUXGRaDxi4u4vJEqBgSThCk9PLtJcEm-weIh6gY-W9FLa8Wp5vBEzjkD1ALMqmD4RQ9CnzmE6GAX-6OyN0pXsG_-7sZC5QbDaxoXJzBqxGesEdJGY51yX_sB0Pq7l34V9XH1BDWhea7yhIpG_C3c9NyhXd0krkr_IuoJLIF_AD8V5JJ5F4Xz6CCRYr5QYbfsBesySf03Ns-9AJycu3XBigWH1KS7XinML6QUtwsA644cfJe9E4mQKyo6RT2Ua5Q_hmkW-u4jmpXhviZutJN1TKGXUKg8PL85MU2uSzFSYuXCEJC7fdYA4F3cCCak4N2JVzOA733LyGban9WPVsu5gNtBmxnL1UC5jBt1Rw2IxL6k3PYAGQAuO6ZkaomK5lX8mApXjU8w2gW_Gl2aTv9tuXcevvZUawi6d5o3AH2vlP0s0opYB2-wupBogTEoZxd0AmIo2IWhrlWmqgdGMjIMFP3CXWhWcs8S_pbuvuJguc9lkWL8fck5m1Dk0OZeEZ5FozLT5W3wPW4FH74XLW4OIinCqYbOvG5_ILGgG1vvocR1Ie125IdgFclcQ3u0co_GfF8kKh7y7bUf4R",
    "challengeName": "NEW_PASSWORD_REQUIRED"
}
```

### 3. 임시비번 변경
[요청]
```
POST http://127.0.0.1:8080/auth/changeTempPassword
{
	"userId": "songaal",
	"password": "123123",
	"session": "Fsof-k3FDfxFajErZAHeOwpmZXl66l1quUpVOQ30oSwAPuH63gbMO6sxqr8N4Okv64a5p3WR0U-46EWxxTJt2ogUItBt-pn6RQPcGlY19t-lp6r7_D_hwh8Jmvts0grWmBh82Gvy5mJgSkewgaUkS3xctDZNTkSgACFHJRMnGpUVSI3yltl51vLWoWboILq8uwE2ANVfnjHdyLIkiUFpYT-CfFQ_q9ZCj2dMnPAfXPh9fz8J6xFUXGRaDxi4u4vJEqBgSThCk9PLtJcEm-weIh6gY-W9FLa8Wp5vBEzjkD1ALMqmD4RQ9CnzmE6GAX-6OyN0pXsG_-7sZC5QbDaxoXJzBqxGesEdJGY51yX_sB0Pq7l34V9XH1BDWhea7yhIpG_C3c9NyhXd0krkr_IuoJLIF_AD8V5JJ5F4Xz6CCRYr5QYbfsBesySf03Ns-9AJycu3XBigWH1KS7XinML6QUtwsA644cfJe9E4mQKyo6RT2Ua5Q_hmkW-u4jmpXhviZutJN1TKGXUKg8PL85MU2uSzFSYuXCEJC7fdYA4F3cCCak4N2JVzOA733LyGban9WPVsu5gNtBmxnL1UC5jBt1Rw2IxL6k3PYAGQAuO6ZkaomK5lX8mApXjU8w2gW_Gl2aTv9tuXcevvZUawi6d5o3AH2vlP0s0opYB2-wupBogTEoZxd0AmIo2IWhrlWmqgdGMjIMFP3CXWhWcs8S_pbuvuJguc9lkWL8fck5m1Dk0OZeEZ5FozLT5W3wPW4FH74XLW4OIinCqYbOvG5_ILGgG1vvocR1Ie125IdgFclcQ3u0co_GfF8kKh7y7bUf4R"
}
```

[결과]
```
{
    "sub": "fc588f0f-071b-4100-be37-229cea6f534e",
    "event_id": "fec86141-3d46-11e8-8aa3-3da2d043bd8b",
    "token_use": "access",
    "scope": "aws.cognito.signin.user.admin",
    "auth_time": 1523423576,
    "iss": "https://cognito-idp.ap-northeast-2.amazonaws.com/ap-northeast-2_8UlVuFFva",
    "exp": 1523427176,
    "iat": 1523423576,
    "jti": "6f1479b3-6862-4f7b-befc-7eb0631e097e",
    "client_id": "4km83jbt1d6pg415q4ieqt41b0",
    "username": "songaal"
}
```

### 4. 로그아웃

[요청]
```
POST http://127.0.0.1:8080/auth/logout
```



## 텔레그램 설정 API

한 유저당 하나의 텔레그램 계정만 연결할 수 있다.

### 1. 계정연결 

[요청]
```
POST /v1/notification/telegram
{
	"title": "songaal@",
	"serviceUser":"352354994"
}
```

- title : 텔레그램 계정 설명
- serviceUser : 텔레그램 ChatID. 코인클라우드봇이 알려준다.

[결과]
```
{
    "userId": "songaal",
    "serviceName": "telegram",
    "serviceUser": "352354994",
    "title": "songaal",
    "createTime": null
}
```

[에러]
```
{
    "message": "[FAIL] Insert UserNotification"
}
```

### 2. 조회
[요청]
```
GET /v1/notification/telegram
```

[결과]
```
{
    "userId": "songaal",
    "serviceName": "telegram",
    "serviceUser": "352354994",
    "title": "songaal",
    "createTime": 1523943171000
}
```
** 설정이 존재하지 않는다면 `200OK`에 `Body`에는 아무것도 리턴되지 않는다.

### 3. 연결삭제
[요청]
```
DELETE /v1/notification/telegram
{
	"serviceUser": "352354994"
}
```

[결과]
```
{
    "userId": "songaal",
    "serviceName": "telegram",
    "serviceUser": "352354994",
    "title": "songaal",
    "createTime": 1523943711000
}
```
** 삭제된 객체가 리턴된다.

** 텔레그램 메신저에서 `/quit` 이라고 입력해도 연결삭제된다. 