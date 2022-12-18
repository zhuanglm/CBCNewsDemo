package com.cbc.newsdemo.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cbc.newsdemo.data.models.Article
import com.cbc.newsdemo.data.models.Image
import com.cbc.newsdemo.data.models.TypeAttributes
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class ArticleDatabaseTest : TestCase() {

    private lateinit var db: ArticleDatabase
    private lateinit var dao: ArticleDao

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    // Override function setUp() and annotate it with @Before
    // this function will be called at first when this test class is called
    @Before
    public override fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
        db = Room.inMemoryDatabaseBuilder(context, ArticleDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()
        dao = db.getArticleDao()
    }

    // Override function closeDb() and annotate it with @After
    // this function will be called at last when this test class is called
    @After
    fun closeDb() {
        db.close()
    }

    fun writeArticleToDB() = runBlocking {
        val artile = Article(
            123,
            "title",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "story",
            null,
            null,
            null,
            TypeAttributes(null, null, null, null, null,
                null, null, null, null, null, null,
                null,null,null,null,null,null,
                null,null,null,null,null,null,null,
                null,null,null),
            Image(null),
            null
        )
        dao.insert(artile)
    }

    @Test
    fun testReadArticle() = runBlocking {
        writeArticleToDB()
        val articles = dao.getAllArticles().blockingObserve()

        Assert.assertNotNull(articles)
        assert(articles?.size == 1)
    }

    @Test
    fun testReadArticleByIDTrue() = runBlocking {
        writeArticleToDB()
        val articles = dao.getArticle(123).blockingObserve()

        Assert.assertNotNull(articles)
        assert(articles?.size == 1)
    }

    @Test
    fun testReadArticleByIDFalse() = runBlocking {
        writeArticleToDB()
        val articles = dao.getArticle(456).blockingObserve()

        assertNotNull(articles)
        assert(articles?.size == 0)
    }

    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> { t ->
            value = t
            latch.countDown()
        }

        observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)
        return value
    }
}