# Sample ToDo App

## 作りたいもの

PostgreSQL + Spring Boot を使った To Do (やること管理) アプリのバックエンド。 以下のAPIエンドポイントを提供する:

  - `GET /todos` を実行すると、データベースにある Todo 項目すべてを JSON形式で返す。
    - ```[{"id":2, "text":"Buy milk."}, {"id":5, "text":"Clean up."}]```
  - `POST /todos` を実行すると、与えられた Todo をデータベースに追加する。
    このとき、新しく追加された Todo ID を返す。
    - ```{"text": "Hello!"}```
  - `GET /todos/ID` を実行すると、与えられた IDをもつ Todo 項目ひとつをJSON形式で返す。
    - ```{"id":2, "text":"Buy milk."}```
  - `DELETE /todos/ID` を実行すると、与えられた IDをもつ Todo 項目をデータベースから削除する。 
  - 存在しないIDに対して `GET` や `DELETE` をおこなうと、ステータスコードとして 404 (Not Found) を返す。

## その他の目標

  - TDDを使って開発し、すべての実装は自動テストによって検証されるようにする。
  - Controller - Service - Repository 層に分かれたクリーンアーキテクチャを実装する。
  - 「Spring流」のやり方を徹底する。
