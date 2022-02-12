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
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.android.codelabs.paging.R
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.ui.UiModel


@Composable
fun RepoUI(repo: Repo){

    ConstraintLayout(
        Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
    ) {
        val starsGuide = createGuidelineFromStart(0.5f)
        val forksGuide = createGuidelineFromStart(0.75f)
        val (fullName, description, language, stars, starsText, forks, forksText) = createRefs()

        Text(repo.fullName, style = MaterialTheme.typography.h5, modifier = Modifier
            .padding(8.dp)
            .constrainAs(fullName) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }, color = colorResource(R.color.titleColor))

        Text(repo.description.toString(), style = MaterialTheme.typography.subtitle1, modifier = Modifier
            .padding(8.dp)
            .constrainAs(description) {
                top.linkTo(fullName.bottom)
                start.linkTo(parent.start)
            }
        )

        Text(stringResource(R.string.language , repo.language.toString()), modifier = Modifier
            .padding(8.dp)
            .constrainAs(language) {
                start.linkTo(parent.start)
                top.linkTo(description.bottom)
                bottom.linkTo(parent.bottom)
            }
            ,
            style = MaterialTheme.typography.caption)


        Icon(painterResource(id = R.drawable.ic_star), "Stars",
            modifier = Modifier
                .constrainAs(stars){
                    start.linkTo(starsGuide)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(description.bottom)
                }

                )
        Text(
            repo.stars.toString(), style = MaterialTheme.typography.caption, modifier = Modifier
                .padding(8.dp)
                .constrainAs(starsText){
                    start.linkTo(stars.end)
                    bottom.linkTo(parent.bottom)
                }
        )


        Icon(painterResource(id = R.drawable.ic_git_branch), "Forks",
            modifier = Modifier
                .constrainAs(forks){
                    start.linkTo(forksGuide)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(description.bottom)
                }
                )
        Text(
            repo.forks.toString(), style = MaterialTheme.typography.caption, modifier = Modifier
                .padding(8.dp)
                .constrainAs(forksText){
                    start.linkTo(forks.end)
                    bottom.linkTo(parent.bottom)
                }
        )


    }

}

@Preview(showBackground = true)
@Composable
fun PreviewRepoItem() {
    val repo = Repo(1, "repo", "author/repo", "An awesome library that you need to do awesome stuff",
    "https://example.com", 20000, 10, "Kotlin")

    RepoUI(repo = repo)


}
