---
title: Thunder API contracts - With Behaviour
---

## Admin Contracts

### Create Behaviour Tags
- Path: `/thunder/behaviour-tags`
- Method: POST
- Headers:
  - `Content-Type: application/json`
  - `user: <string>`
- Request:
  - Body: Behaviour Tag create payload
- Responses:
  - 200: `{}`  
  - 400:

```json
{
  "error": {
    "message": "Duplicate behvaiour tag name",
    "code": "CAMPAIGN_DUPLICATE_NAME"
  }
}
```

### Update Behaviour Tag
- Path: `/thunder/behvaiour-tags/:behaviourTagName`
- Method: PUT
- Params:
  - `behaviourTagName` (required)
- Headers:
  - `Content-Type: application/json`
  - `user: <string>`
- Request:
  - Body: Behaviour Tag update payload
- Responses:
  - 200: `{}`  
  - 400:

```json
{
  "error": {
    "message": "Invalid behaviour tag",
    "code": "CAMPAIGN_INVALID"
  }
}
```

### Get Behaviour Tags
- Path: `/thunder/behaviour-tags`
- Method: GET
- Headers:
  - `Content-Type: application/json`
- Request: none
- Response: list of behaviour tags

```json
{
  "data": [
    // ...
  ]
}
```

### Get list of CTAs
- Path: `/thunder/ctas`
- Method: GET
- Headers:
  - `Content-Type: application/json`
- Request: none
- Response: list of CTAs

```json
{
  "data": [
    // ...
  ]
}
```

### Get details of CTA
- Path: `/thunder/ctas/:ctaId`
- Method: GET
- Headers:
  - `Content-Type: application/json`
- Request: none
- Response: CTA details

```json
{
  "data": {
    // cta detail for :ctaId
  }
}
```

### Get Filter Values
- Path: `/thunder/filters`
- Method: GET
- Headers:
  - `Content-Type: application/json`
- Request: none
- Responses:
  - 200:

```json
{}
```

or

```json
{
  "data": {
    "names": ["Integration test"],
    "teams": ["A", "B", "D"],
    "tags": ["P", "Q"],
    "createdBy": [
      "vishnu@dream11.com",
      "raj@dream11.com",
      "bhargav@dream11.com"
    ]
  }
}
```

### Create CTA
- Path: `/thunder/ctas`
- Method: POST
- Headers:
  - `Content-Type: application/json`
  - `user: <string>`
- Request:
  - Body: CTA create payload
- Responses:
  - 200: `{}`  
  - 400:

```json
{
  "error": {
    "message": "Duplicate cta name",
    "code": "CTA_DUPLICATE_NAME"
  }
}
```

### Update CTA
- Path: `/thunder/ctas/:ctaId`
- Method: PUT
- Params:
  - `ctaId` (required)
- Headers:
  - `Content-Type: application/json`
- Request:
  - Body: CTA update payload
- Responses:
  - 200: `{}`  
  - 400:

```json
{
  "error": {
    "message": "Invalid cta id",
    "code": "CTA_ID_INVALID"
  }
}
```

### Update CTA status
- Paths:
  - `/thunder/ctas/:ctaId/live`
  - `/thunder/ctas/:ctaId/pause`
  - `/thunder/ctas/:ctaId/conclude`
  - `/thunder/ctas/:ctaId/terminate`
- Method: PUT
- Headers:
  - `Content-Type: application/json`
  - `user: <string>`
  - `timestamp: <string>`
- Request:
  - Body: status update payload
- Responses:
  - 200: `{}`  
  - 400 (invalid id):

```json
{
  "error": {
    "message": "Invalid cta id",
    "code": "CTA_ID_INVALID"
  }
}
```

  - 400 (invalid status):

```json
{
  "error": {
    "message": "Invalid status",
    "code": "CTA_STATUS_INVALID"
  }
}
```

  - 400 (invalid status change):

```json
{
  "error": {
    "message": "Invalid status change",
    "code": "CTA_STATUS_UPDATE_INVALID"
  }
}
```

## Frontend Contracts

### Fetch CTAs (and update delta)
- Path: `/cta/active/state-machines`
- Method: POST
- Headers:
  - `Content-Type: application/json`
  - `auth-userid: <string>`
  - `app_version: <string>`
  - `codepush_version: <string>`
  - `package_name: <string>`
- Request:
  - Body: campaigns request payload
- Response: app launch response

### Update CTA Delta
- Path: `/cta/state-machines/snapshot/delta/`
- Method: POST
- Headers:
  - `Content-Type: application/json`
  - `auth-userid: <string>`
  - `app_version: <string>`
  - `codepush_version: <string>`
  - `package_name: <string>`
- Request:
  - Body: snapshot delta request
- Responses:
  - 200:

```json
{}
```

or

```json
{
  "data": true
}
```

  - 400:

```json
{
  "error": {
    "message": "Invalid request",
    "code": "INVALID_REQUEST"
  }
}
```


