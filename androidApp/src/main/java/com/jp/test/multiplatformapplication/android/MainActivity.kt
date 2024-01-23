package com.jp.test.multiplatformapplication.android

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jetbrains.handson.kmm.shared.SpaceXSDK
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory
import com.jetbrains.handson.kmm.shared.entity.RocketLaunch
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val launchesRvAdapter = LaunchesRvAdapter(listOf())
    private val sdk = SpaceXSDK(DatabaseDriverFactory(this))
    private lateinit var launchesRecyclerView: RecyclerView
    private lateinit var progressBarView: FrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "SpaceX Launches"
        setContentView(R.layout.activity_main)

        launchesRecyclerView = findViewById(R.id.launchesListRv)
        progressBarView = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeContainer)

        launchesRecyclerView.adapter = launchesRvAdapter
        launchesRecyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            displayLaunches(true)
        }

        displayLaunches(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    private fun displayLaunches(needReload: Boolean) {
        progressBarView.visibility = View.VISIBLE
        mainScope.launch {
            kotlin.runCatching {
                sdk.getLaunches(needReload)
            }.onSuccess {
                launchesRvAdapter.launches = it
                launchesRvAdapter.notifyDataSetChanged()
            }.onFailure {
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            progressBarView.visibility = View.GONE
        }
    }
}


@Composable
fun LaunchListView(launches: List<RocketLaunch>) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(launches) { launch ->
            LaunchCard(launch)
        }
    }
}


@Composable
fun LaunchCard(launch: RocketLaunch) {

    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 1.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {

        Text(text = stringResource(R.string.mission_name_field, launch.missionName))
        Spacer(modifier = Modifier.height(4.dp))
        GetLaunchSuccessView(launch)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(R.string.launch_year_field, launch.launchYear.toString()))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(R.string.details_field, launch.details ?: ""))
        Spacer(modifier = Modifier.height(4.dp))
    }

}


@Composable
fun GetLaunchSuccessView(launch: RocketLaunch) {
    val launchSuccess = launch.launchSuccess
    if (launchSuccess != null) {
        if (launchSuccess) {
            Text(
                text = stringResource(R.string.successful),
                color = colorResource(id = R.color.colorSuccessful)
            )
        } else {
            Text(
                text = stringResource(R.string.unsuccessful),
                color = colorResource(id = R.color.colorUnsuccessful)
            )
        }
    } else {
        Text(
            text = stringResource(R.string.no_data),
            color = colorResource(id = R.color.colorNoData)
        )
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
//        GreetingView(listOf("Hello, Android!"))
    }
}
