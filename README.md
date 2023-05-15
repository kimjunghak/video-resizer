## 실행 방법

1. ./application.yml 파일의 아래 내용을 수정
```yaml
path:
  video: /path/to/save/video/file
  thumbnail: /path/to/save/thumbnail/file
```
2. ./conf/application.yml 파일과 ./lib/resizer-0.0.1-SNAPSHOT.jar 파일정보는 docker-compose.yml 에서 환경을 수정 가능
3. 이외의 내용은 테스트 환경에서 맞춰서 수정
4. restart.sh 파일을 실행가능 파일로 변경 -> chmod +x restart.sh
5. restart.sh 파일을 실행


## API 정보

1. **영상 업로드 및 변환 API**

- form data key => `file`

```http request
POST http://localhost:8080/video/resize
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="test.mp4"

< /path/to/upload/file

--WebAppBoundary--
```

```json
"ok"
```

2. **영상 상세 정보 조회 API**

```http request
GET http://localhost:8080/video/1
Accept: application/json
```

```json
{
  "id": 1,
  "title": "test.mp4",
  "thumbnailUrl": "http://localhost:8080/thumbnailtest_thumbnail.jpg",
  "original": {
    "fileSize": 63310586,
    "width": 3840,
    "height": 2160,
    "videoUrl": "http://localhost:8080/video/test.mp4"
  },
  "resized": {
    "fileSize": 2033697,
    "width": 360,
    "height": 202,
    "videoUrl": "http://localhost:8080/video/test_resize.mp4"
  },
  "createdAt": "2023-05-15T20:54:37.936352"
}
```

3. **영상 변환 진행률 조회 API**

```http request
GET http://localhost:8080/video/1/progress
Accept: application/json
```

```json
{
  "id": 1,
  "progress": "13%"
}
```

4. **업로드한 영상 Stream API**

- {업로드 파일}

```http request
GET http://localhost:8080/stream/video/test.mp4
Accept: application/json
```

```json
"video file web에서 확인"
```

5. **추출한 썸네일 조회 API**

- {업로드 파일이름}_thumbnail.jpg

```http request
GET http://localhost:8080/thumbnail/test_thumbnail.jpg
Accept: application/json
```
