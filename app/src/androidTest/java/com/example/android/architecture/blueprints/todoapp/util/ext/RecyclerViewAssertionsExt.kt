package com.example.android.architecture.blueprints.todoapp.util.ext

import android.view.View
import android.view.View.FIND_VIEWS_WITH_TEXT
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAssertion
import com.google.common.truth.Truth
import org.hamcrest.Matcher
import org.junit.Assert

object RecyclerViewAssertions {
    fun isEmptyList() = hasItemsCount(0)

    fun hasItemsCount(count: Int): ViewAssertion {
        return ViewAssertion { view, e ->
            if (view !is RecyclerView) {
                throw e!!
            }
            Truth.assertThat(view.adapter!!.itemCount).isEqualTo(count)
        }
    }

    fun hasHolderItemAtPosition(
        index: Int,
        viewHolderMatcher: Matcher<RecyclerView.ViewHolder?>?
    ): ViewAssertion {
        return ViewAssertion { view, e ->
            if (view !is RecyclerView) {
                throw e!!
            }
            Assert.assertThat(view.findViewHolderForAdapterPosition(index), viewHolderMatcher)
        }
    }

    fun hasViewWithTextAtPosition(index: Int, text: CharSequence): ViewAssertion {
        return ViewAssertion { view, e ->
            if (view !is RecyclerView) {
                throw e!!
            }
            val outviews: ArrayList<View> = ArrayList()
            view.findViewHolderForAdapterPosition(index)!!.itemView.findViewsWithText(
                outviews, text,
                FIND_VIEWS_WITH_TEXT
            )
            Truth.assert_()
                .withMessage("There's no view at index $index of recyclerview that has text : $text")
                .that(outviews).isNotEmpty()
        }
    }

    fun doesntHaveViewWithText(text: String?): ViewAssertion {
        return ViewAssertion { view, e ->
            if (view !is RecyclerView) {
                throw e!!
            }
            val rv = view
            val outviews: ArrayList<View> = ArrayList()
            for (index in 0 until rv.adapter!!.itemCount) {
                rv.findViewHolderForAdapterPosition(index)!!.itemView.findViewsWithText(
                    outviews, text,
                    FIND_VIEWS_WITH_TEXT
                )
                if (outviews.size > 0) break
            }
            Truth.assertThat(outviews).isEmpty()
        }
    }
}
