# dashj-interface
Smart wrapper for DashJ library making development of Dash enabled apps for Android much easier.

## Code samples

Displaying blockchain sync progress: 
```kotlin
viewModel.blockchainState.observe(this, Observer {
    messageView.text =
            "bestChainDate: ${it!!.bestChainDate}\n" +
            "bestChainHeight: ${it.bestChainHeight}\n" +
            "blocksLeft: ${it.blocksLeft}\n"
})
```

Sending funds: 
```kotlin
viewModel.sendFunds("yi8eWv9S3pmHo5ZLuQn6ftwik4AYN2WHd6", Coin.COIN,
        object : WalletAppKitService.Result<Transaction> {

            override fun onSuccess(tx: Transaction) {
                messageView.text = "Sent: ${tx.hashAsString}"
            }

            override fun onFailure(ex: Exception) {
                messageView.text = "Failure: ${ex.message}"
            }
        }
)
```

Add dashj-interface to your project
----------------------------

Via Gradle:
```gradle
implementation 'org.dashj:dashj-interface:0.9.2'
```

Via Maven:
```xml
<dependency>
  <groupId>org.dashj</groupId>
  <artifactId>dashj-interface</artifactId>
  <version>0.9.2</version>
</dependency>
```
