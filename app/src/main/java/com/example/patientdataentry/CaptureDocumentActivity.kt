package com.example.patientdataentry

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.canhub.cropper.CropImageView
import java.io.File
import java.util.*


class CaptureDocumentActivity : AppCompatActivity() {

    private lateinit var uri: Uri
    private lateinit var cropImageView: CropImageView
    private lateinit var uploadDocConstraintLayout: ConstraintLayout
    private lateinit var cropDocConstraintLayout: ConstraintLayout
    private lateinit var cancelButton: Button
    private lateinit var doneButton: Button
    private lateinit var closeImageView: ImageView
    private lateinit var tickDoneImageView: ImageView
    private lateinit var setCrooppedImageView: ImageView
    private lateinit var leadId : String
    private lateinit var finalBase64EncodedImg : String
    private lateinit var pbProgress: ProgressBar
    private var permissionDocumentType = 1
    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 44
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE")

    var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uriResult ->
            uriResult?.let {
                Log.e("pickImage", "Image picked " + uriResult)
                setCrooppedImageView.setImageURI(Uri.parse("content://media/external/images/media/80831"))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_document)

        cropImageView = findViewById(R.id.cropImageView)
        setCrooppedImageView = findViewById(R.id.cropped_imageView)

        /*cropDocConstraintLayout = findViewById(R.id.crop_constraintLayout)
        cancelButton = findViewById(R.id.cancel_button)
        doneButton = findViewById(R.id.done_button)
        doneButton = findViewById(R.id.done_button)
        closeImageView = findViewById(R.id.close_imageview)
        tickDoneImageView = findViewById(R.id.tick_imageview)

        pbProgress = findViewById(R.id.pb_progress)*/

        /*val bundle :Bundle? = intent.extras
        if (bundle!=null){
            leadId = bundle.getString(ConstantKeys.KEY_DEEPLINK_LEADID).toString()
            Log.e("bundle", "leadId $leadId ,userId $userId ,userName $userName " +
                    ",userEmailId $userEmailId ,mode $mode"+"caseId $caseId")
        }*/



        pickImage.launch("image/*")

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        var fileName1 = ""
        fileName1 = "IMG_8557864401318610507.jpg"

        val dest: String = this@CaptureDocumentActivity.getExternalFilesDir(null).toString() + "/"
        val imgFile = File("$dest/$fileName1")
        if (!imgFile.exists()) {
            imgFile.mkdir()
        }

        if (imgFile != null && imgFile.exists()) //Checking for the file is exist or not
        {
            val bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.patientdataentry/files/Pictures/IMG_8557864401318610507.jpg")
            setCrooppedImageView.setImageBitmap(bitmap)
        }

        val absolutePath : String = photoFile.absolutePath
        Log.e("photo-absolutePath", absolutePath)
        val name : String = photoFile.name
        Log.e("photo-name", name)
        val nameWithoutExtension : String = photoFile.nameWithoutExtension
        Log.e("photo-nameWithoutExt", nameWithoutExtension)
        val path : String = photoFile.path
        Log.e("photo-path", path)

        uri = FileProvider.getUriForFile(
                applicationContext,
                "${applicationContext.packageName}.provider",
                photoFile
        )

        val isAllowPermission = allPermissionsGranted()
        Log.e("abx", "Permission Granted $isAllowPermission")
        if (!isAllowPermission){
            requestPermissions(REQUIRED_PERMISSIONS, ASK_MULTIPLE_PERMISSION_REQUEST_CODE)
        }else{
            Log.e("abx", "Already Permission Granted ")
            takePicture.launch(uri)
        }

       /* cancelButton.setOnClickListener {
            finish()
        }

        doneButton.setOnClickListener {

            var cropped = (setCrooppedImageView.drawable as BitmapDrawable).bitmap

            val stamp = Timestamp(System.currentTimeMillis())
            val date = Date(stamp.time)

            //finalBase64EncodedImg = SalesforceLoginImplementation.compressImage500(cropped)

            pbProgress.visibility = View.VISIBLE

        }

        closeImageView.setOnClickListener {
            takePicture.launch(uri)
        }

        tickDoneImageView.setOnClickListener {
            val cropped: Bitmap? = cropImageView.croppedImage
            val croppedUri: Uri? = cropImageView.customOutputUri
            val croppedImageUri: Uri? = cropImageView.imageUri

            cropImageView.visibility = View.GONE
            setCrooppedImageView.visibility = View.VISIBLE
            setCrooppedImageView.setImageBitmap(cropped)
            cropDocConstraintLayout.visibility = View.GONE
            uploadDocConstraintLayout.visibility = View.VISIBLE
        }*/

    }


    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                    takePicture.launch(uri)
                    /*btn_getImg.visibility = View.VISIBLE
                    cropImageView.setImageBitmap(null)
                    setCropImageView.setImageBitmap(null)
                    cropImageView.visibility = View.GONE
                    setCropImageView.visibility = View.GONE*/
                //}
            }
            return
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            Log.e("abx", "aserfd $isSaved")

            cropImageView.setImageUriAsync(uri)

            /*cropDocConstraintLayout.visibility = View.VISIBLE*/
        }
    }

}