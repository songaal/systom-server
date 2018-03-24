
# coincloud server

### REST API 
##### URL http://loclahost:8080/v1


- ## 알고리즘 소스 조회
	- #### 경로
		- /algos/{algoId} (GET)
	- #### 파라미터 
	- 없음

- ## 알고리즘 전체 조회
	- #### 경로
		- /algos/me (GET)
	- #### 해더
		- userId

- ## 알고리즘 생성
	- #### 경로
		- /algos (POST)
	- #### 파라미터 
		- userId
		- source
 
- ## 알고리즘 수정
	- #### 경로
		- /algos/{algoId} (PUT)
	- #### 파라미터 
		- source
 
- ## 알고리즘 실행
	- #### 경로
		- /tasks (POST)
	- #### 파라미터 
		- algoId
		- exchangeName
		- baseCurrency
		- capitalBase
		- live
		- simulationOrder
		- start
		- end
		- dataFrequency
 


# example
- ### 알고리즘 생성
```
curl -X POST \
  http://api.gncloud.io:8080/v1/algos \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -H 'postman-token: 62d602c8-c447-e7c3-c501-65ccbbeab63f' \
  -F userId=joonwoo \
  -F 'source=def initialize(context):
    print('\''----------initialize--------'\'')
    context.asset = symbol('\''btc_usdt'\'')


def handle_data(context, data):
    print('\''----------handle_data--------'\'')
    order(context.asset, 1)
    # record(btc=data.current(context.asset, '\''price'\''))

def analyze(context, stats):
    print('\''----------analyze--------'\'')'
```
- ### 알고리즘 전체 조회
```
curl -X GET \
  http://api.gncloud.io:8080/v1/algos/me \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 73941d38-69d5-5f75-28f4-3e6a702af628' \
  -H 'userid: joonwoo'
```
- ### 알고리즘 시작
```
curl -X POST \
  http://api.gncloud.io:8080/v1/tasks \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -H 'postman-token: 9517310f-6d93-bdb3-a194-e916986e49e0' \
  -F algoId=808d3ce1-e9db-4f0c-80aa-0c9bf97f2207 \
  -F exchangeName=bitfinex \
  -F baseCurrency=usd \
  -F capitalBase=1000 \
  -F live=false \
  -F simulationOrder=true \
  -F start=2015-09-22 \
  -F end=2017-10-01 \
  -F dataFrequency=minute```
