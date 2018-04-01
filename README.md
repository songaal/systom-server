
# coincloud server

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
            - start
            - end
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
