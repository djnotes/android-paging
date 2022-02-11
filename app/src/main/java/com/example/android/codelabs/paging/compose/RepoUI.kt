package com.example.android.codelabs.paging.compose
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.ui.UiModel


@Composable
fun RepoUI(repo: Repo){
    Column(
        Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
    ) {
        Text(repo.name, style = MaterialTheme.typography.h5, modifier = Modifier
            .padding(8.dp), color = colorResource(R.color.titleColor))

        Text(repo.description.toString(), style = MaterialTheme.typography.subtitle1, modifier = Modifier
            .padding(8.dp))

        Row(modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.language , repo.language.toString()), modifier = Modifier
                .padding(8.dp),
            style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.width(48.dp))

            Icon(painterResource(id = R.drawable.ic_star), "Stars")
            Text(repo.stars.toString(), style = MaterialTheme.typography.caption , modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))


            Icon(painterResource(id = R.drawable.ic_git_branch), "Forks")
            Text(repo.forks.toString(), style = MaterialTheme.typography.caption , modifier = Modifier
                .padding(8.dp))

        }


    }

}

@Preview(showBackground = true)
@Composable
fun PreviewRepoItem() {
    val repo = Repo(1, "author/repo", "An Awesome Repo", "An awesome library that you need to do awesome stuff",
    "https://example.com", 100, 10, "Kotlin")

    RepoUI(repo = repo)


}
