package com.example.kalepa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kalepa.common.extra
import com.example.kalepa.common.getIntent
import com.example.kalepa.common.loadImage
import com.example.kalepa.models.Product
import kotlinx.android.synthetic.main.activity_product.*

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_product_image.*
import kotlinx.android.synthetic.main.fragment_product_image.view.*

class ProductActivity : AppCompatActivity() {

    val product: Product by extra(PRODUCT_ARG)
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var num_images = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        /*val date = product.Upload_date.day.toString() + "/" + product.Upload_date.month.toString() +
                "/" + product.Upload_date.year.toString()*/

        //b_product_images.loadImage(product.Images[0]!!)
        b_product_name.setText(product.title)
        b_product_price.setText(product.price.toString())
        b_product_description.setText(product.descript)
        b_product_date.setText(product.publish_date)
        b_product_seller.setText("AHORA ES UN ID :/")
        //b_product_rating

        b_product_seller.setOnClickListener {
            ProfileActivity.start(this, product.user_id.toString())
        }

        num_images = product.photo_urls.size

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        b_product_images_container.adapter = mSectionsPagerAdapter
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position , product.photo_urls)
        }

        override fun getCount(): Int {
            // Show 5 total pages.(we will use 5 pages so change it to 5)
            return num_images
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_product_image, container, false)

            val images = arguments!!.getStringArrayList(ARG_IMAGE_LIST)
            rootView.image_iv.loadImage(images[arguments!!.getInt(ARG_SECTION_NUMBER)])

            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            private val ARG_IMAGE_LIST = "image_list"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int, images: ArrayList<String?>): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                args.putStringArrayList(ARG_IMAGE_LIST, images)
                fragment.arguments = args
                return fragment
            }
        }
    }

    companion object {

        private const val bullet = '\u2022'
        private  const val PRODUCT_ARG = "com.example.kalepa.ProductActivity.ProductArgKey"

        fun getIntent(context: Context, product: Product) = context
            .getIntent<ProductActivity>()
            .apply { putExtra(PRODUCT_ARG, product) }

        fun start(context: Context, product: Product) {
            val intent = getIntent(context, product)
            context.startActivity(intent)
        }
    }
}
