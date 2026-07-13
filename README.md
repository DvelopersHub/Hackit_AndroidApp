# Hackit Android

ハッカソンイベント **Hackit** の参加者向け Android アプリ。
[iOS 版 (hackit-ios)](https://github.com/KIT-DevelopersHub/hackit-ios) と機能パリティを取っている。

## 目次

- [画面構成](#画面構成)
- [アーキテクチャ](#アーキテクチャ)
- [ビルド](#ビルド)
- [テスト](#テスト)
- [API 接続先の切り替え](#api-接続先の切り替え)
- [リリース前チェックリスト](#リリース前チェックリスト)

## 画面構成

| 画面 | 内容 | iOS 対応 |
|---|---|---|
| サインイン | 12 プロバイダ（主要 4 + 折りたたみ 8） | `SignInView` |
| メール/電話入力 | ドラフト入力 + バリデーション | `EmailEntryView` / `PhoneEntryView` |
| イベント情報 | イベント概要・自分の受付状態・チームメンバー一覧（5 秒ポーリング） | `EventInfoView` |
| 通知 | 通知タイムライン + 手動受付フェイルセーフ | `NotificationView` |
| サイドバー | サインアウト | `SidebarView` |

## アーキテクチャ

- **Kotlin + Jetpack Compose (Material 3)**、単一 Activity
- **MVVM + Repository**（iOS の ADR-0001 と同じ構成）
- **Mock-first**（iOS の ADR-0004 と同じ）: `data/mock/` の Mock リポジトリで動作する。
  実 API 確定後は `AppContainer` で実装クラスに差し替えるだけでよい
- 位置情報ジオフェンスによる自動受付は iOS 同様 **次イテレーション**（現状は手動受付フェイルセーフのみ）

## ビルド

```
./gradlew assembleDebug
```

- JDK 17 以上 / Android SDK Platform 36 が必要
- `local.properties` に `sdk.dir` を設定する（Android Studio が自動生成）

## テスト

```
./gradlew testDebugUnitTest
```

- ViewModel 単体テスト（kotlinx-coroutines-test）
- 主要フローの E2E は **Robolectric + Compose UI Test**（エミュレータ不要）:
  サインイン → イベント情報 → 通知 → 手動受付

## API 接続先の切り替え

`BuildConfig.API_BASE_URL` は Gradle プロパティで差し替えられる（GCP デプロイ後に指定）:

```
./gradlew assembleRelease -PhackitApiBaseUrl=https://api.example.com
```

未指定時はプレースホルダ（`https://hackit-api.example.invalid`）。

## リリース前チェックリスト（Google Play）

- [x] `targetSdk 36`（Play の最新要件を満たす）
- [x] `applicationId` を `com.example.*` から `com.kitdevelopershub.hackit` に変更（`com.example` は Play に提出不可）
- [x] アダプティブアイコン（`mipmap-anydpi-v26` + monochrome 対応）
- [x] release ビルドで R8 minify + resource shrink 有効
- [x] 不要な権限なし（位置情報はジオフェンス実装時に追加）
- [ ] 署名鍵（upload keystore）の作成と `signingConfigs` 設定
- [ ] プライバシーポリシー URL の用意（Play Console で必須）
- [ ] Play Console のデータセーフティフォーム記入（現状は収集データなし・実 API/認証接続後に更新）
- [ ] ストア掲載情報（スクリーンショット・説明文・512px アイコン・フィーチャーグラフィック）
- [ ] 内部テストトラックへの AAB アップロード（`./gradlew bundleRelease`）
