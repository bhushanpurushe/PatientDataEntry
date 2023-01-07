package com.example.patientdataentry

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.patientdataentry.database.db.PatientAppDB
import com.example.patientdataentry.database.entity.PatientDataEntity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.text.*
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var nameTxtInpEditTxt : TextInputEditText
    private lateinit var nameTxtInputLayout : TextInputLayout
    private lateinit var ageTxtInpEditTxt : TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    lateinit var radioButton: RadioButton
    private lateinit var mbNoTxtInpEditTxt : TextInputEditText
    private lateinit var emailTxtInpEditTxt : TextInputEditText
    private lateinit var docNameTxtInpEditTxt : TextInputEditText
    private lateinit var docEmailTxtInpEditTxt : TextInputEditText
    private lateinit var baldnessTxtInpEditTxt : TextInputEditText
    private lateinit var hairTypeTxtInpEditTxt : TextInputEditText
    private lateinit var surgPlanSuggTxtInpEditTxt : AutoCompleteTextView
    private lateinit var surgPlanOptTxtInpEditTxt : AutoCompleteTextView
    private lateinit var surDtTiTxtInpEditTxt : TextInputEditText
   /* private lateinit var value1TxtInpEditTxt : TextInputEditText
    private lateinit var value2TxtInpEditTxt : TextInputEditText*/
    private lateinit var saveButton : Button
    private lateinit var patientPhotoButton : Button
    private lateinit var patientScalpBeforeButton : Button
//    private lateinit var patientScalpAfterButton : Button
//    private lateinit var patientUploadScreenshotButton : Button
    /*private lateinit var getButton : Button*/

    private var patientImage: Image? = null
    private var beforeScalpImage: Image? = null
    private var afterScalpImage: Image? = null
    private var screenshotImage: Image? = null

    /*private lateinit var cropImageView: CropImageView*/
    private lateinit var setProfilePhotoImageView: ImageView
    private lateinit var setBeforeScalpImageView: ImageView
//    private lateinit var setAfterScalpImageView: ImageView
//    private lateinit var setUploadScreenshotImageView: ImageView

    private var patientPhotoAbsolutePath: String? = ""
    private var beforeScalpAbsolutePath: String? = ""
    private var afterScalpAbsolutePath: String? = ""

    private lateinit var patientPhotoBitmap: Bitmap
    private lateinit var beforeScalpBitmap: Bitmap
    private lateinit var afterScalpBitmap: Bitmap

    private var patientPhotoUri : Uri? = null
    private var patientBeforeScalpPhotoUri : Uri? = null
    private var patientAfterScalpPhotoUri : Uri? = null
    private var imagePickedScreenshotUri : Uri? = null

    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 44
    private val BEFORE_SCALP_PERMISSION__REQUEST_CODE = 45
    private val AFTER_SCALP_PERMISSION_REQUEST_CODE = 46
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE")


    private val patientAppDB by lazy { PatientAppDB.getAppDb(this)?.patientdao() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameTxtInpEditTxt = findViewById(R.id.nameTextInputEditText)
        nameTxtInputLayout = findViewById(R.id.nameTextInputLayout)
        ageTxtInpEditTxt = findViewById(R.id.ageTextInputEditText)
        genderRadioGroup = findViewById(R.id.radiogender)
        mbNoTxtInpEditTxt = findViewById(R.id.mobileTextInputEditText)
        emailTxtInpEditTxt = findViewById(R.id.emailTextInputEditText)
        docNameTxtInpEditTxt = findViewById(R.id.drNameTextInputEditText)
        docEmailTxtInpEditTxt = findViewById(R.id.drEmailTextInputEditText)
        baldnessTxtInpEditTxt = findViewById(R.id.baldnessGradeTextInputEditText)
        hairTypeTxtInpEditTxt = findViewById(R.id.hairTypeTextInputEditText)
        surgPlanSuggTxtInpEditTxt = findViewById(R.id.surgicalPlanSuggTextInputEditText)
        surgPlanOptTxtInpEditTxt = findViewById(R.id.surgicalPlanOptedTextInputEditText)
        surDtTiTxtInpEditTxt = findViewById(R.id.surgeryDtTiTextInputEditText)
        var surgeryPlans = resources.getStringArray(R.array.SurgeryPlans)
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, surgeryPlans)

        surgPlanSuggTxtInpEditTxt.setAdapter(adapter)
        surgPlanOptTxtInpEditTxt.setAdapter(adapter)

       /* value1TxtInpEditTxt = findViewById(R.id.value1TextInputEditText)
        value2TxtInpEditTxt = findViewById(R.id.value2TextInputEditText)*/

        nameTxtInpEditTxt.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO)

        saveButton = findViewById(R.id.save_button)
        patientPhotoButton = findViewById(R.id.patient_photo_button)
        patientScalpBeforeButton = findViewById(R.id.scalp_before_photo_button)
//        patientScalpAfterButton = findViewById(R.id.scalp_after_photo_button)
//        patientUploadScreenshotButton = findViewById(R.id.upload_screenshot_button)
       /* getButton = findViewById(R.id.get_button)*/

        //cropImageView = findViewById(R.id.cropImageView)
        setProfilePhotoImageView = findViewById(R.id.profile_imageView)
        setBeforeScalpImageView = findViewById(R.id.before_scalp_imageView)
//        setAfterScalpImageView = findViewById(R.id.after_scalp_imageView)
//        setUploadScreenshotImageView = findViewById(R.id.upload_screenshots_imageView)

        val date = getCurrentDate()
        System.out.println(" today date is - $date")
        surDtTiTxtInpEditTxt.setText(date)




        /*surDtTiTxtInpEditTxt.value = date*/

        /*getButton.setOnClickListener {

            lifecycleScope.launch {
                patientAppDB?.getPatientDataList()?.collect { patientDataList ->
                    if (patientDataList.isNotEmpty()) {
                        patientDataList.forEach {
                            println(it.patient_id)
                            println(it.patient_name)
                            println(it.patient_email)
                            println(it.patient_hair_type)
                        }
                    }
                }
            }

        }*/

        patientPhotoButton.setOnClickListener {

            val photoFile = File.createTempFile(
                "IMG_",
                ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            patientPhotoUri = FileProvider.getUriForFile(
                applicationContext,
                "${applicationContext.packageName}.provider",
                photoFile
            )

            patientPhotoAbsolutePath = photoFile.absolutePath
            Log.e("photo-absolutePath", patientPhotoAbsolutePath.toString())

            val name : String = photoFile.name
            Log.e("photo-name", name)
            val nameWithoutExtension : String = photoFile.nameWithoutExtension
            Log.e("photo-nameWithoutExt", nameWithoutExtension)
            val path : String = photoFile.path
            Log.e("photo-path", path)

            val isAllowPermission = allPermissionsGranted()
            Log.e("abx", "Permission Granted $isAllowPermission")
            if (!isAllowPermission){
                requestPermissions(REQUIRED_PERMISSIONS, ASK_MULTIPLE_PERMISSION_REQUEST_CODE)
            }else{
                Log.e("abx", "Already Permission Granted ")
                takePatientPhotoPicture.launch(patientPhotoUri)
            }

            /*var fileName1 = ""
            fileName1 = "IMG_8557864401318610507.jpg"

            val dest: String = this@MainActivity.getExternalFilesDir(null).toString() + "/"
            val imgFile = File("$dest/$fileName1")
            if (!imgFile.exists()) {
                imgFile.mkdir()
            }

            if (imgFile != null && imgFile.exists()) //Checking for the file is exist or not
            {
                val bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.patientdataentry/files/Pictures/IMG_8557864401318610507.jpg")
                setCrooppedImageView.setImageBitmap(bitmap)
            }*/


        }

        patientScalpBeforeButton.setOnClickListener {

            val photoBeforeScalpFile = File.createTempFile(
                "IMG_",
                ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            patientBeforeScalpPhotoUri = FileProvider.getUriForFile(
                applicationContext,
                "${applicationContext.packageName}.provider",
                photoBeforeScalpFile
            )

            beforeScalpAbsolutePath = photoBeforeScalpFile.absolutePath
            Log.e("photo-absolutePath", beforeScalpAbsolutePath.toString())

            val name : String = photoBeforeScalpFile.name
            Log.e("photo-name", name)
            val nameWithoutExtension : String = photoBeforeScalpFile.nameWithoutExtension
            Log.e("photo-nameWithoutExt", nameWithoutExtension)
            val path : String = photoBeforeScalpFile.path
            Log.e("photo-path", path)

            val isAllowPermission = allPermissionsGranted()
            Log.e("abx", "Permission Granted $isAllowPermission")
            if (!isAllowPermission){
                requestPermissions(REQUIRED_PERMISSIONS, BEFORE_SCALP_PERMISSION__REQUEST_CODE)
            }else{
                Log.e("abx", "Already Permission Granted ")
                takepatientBeforeScalpPhotoPicture.launch(patientPhotoUri)
            }

        }

//        patientScalpAfterButton.setOnClickListener {
//
//            val photoAfterScalpFile = File.createTempFile(
//                "IMG_",
//                ".jpg",
//                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            )
//
//            patientAfterScalpPhotoUri = FileProvider.getUriForFile(
//                applicationContext,
//                "${applicationContext.packageName}.provider",
//                photoAfterScalpFile
//            )
//
//            afterScalpAbsolutePath = photoAfterScalpFile.absolutePath
//            Log.e("photo-absolutePath", afterScalpAbsolutePath.toString())
//
//            val name : String = photoAfterScalpFile.name
//            Log.e("photo-name", name)
//            val nameWithoutExtension : String = photoAfterScalpFile.nameWithoutExtension
//            Log.e("photo-nameWithoutExt", nameWithoutExtension)
//            val path : String = photoAfterScalpFile.path
//            Log.e("photo-path", path)
//
//            val isAllowPermission = allPermissionsGranted()
//            Log.e("abx", "Permission Granted $isAllowPermission")
//            if (!isAllowPermission){
//                requestPermissions(REQUIRED_PERMISSIONS, AFTER_SCALP_PERMISSION_REQUEST_CODE)
//            }else{
//                Log.e("abx", "Already Permission Granted ")
//                takepatientAterScalpPhotoPicture.launch(patientAfterScalpPhotoUri)
//            }
//
//        }

//        patientUploadScreenshotButton.setOnClickListener {
//            pickImage.launch("image/*")
//        }


        saveButton.setOnClickListener {

            val getNameTxtInpEditTxt = nameTxtInpEditTxt.text.toString()
            val getAgeTxtInpEditTxt = ageTxtInpEditTxt.text.toString()

            val selectedOption: Int = genderRadioGroup.checkedRadioButtonId
            radioButton = findViewById(selectedOption)
            //Toast.makeText(baseContext, radioButton.text, Toast.LENGTH_SHORT).show()
            val getMbNoTxtInpEditTxt = mbNoTxtInpEditTxt.text.toString()
            val getEmailTxtInpEditTxt = emailTxtInpEditTxt.text.toString()
            val getDocNameTxtInpEditTxt = docNameTxtInpEditTxt.text.toString()
            val getDocEmailTxtInpEditTxt = docEmailTxtInpEditTxt.text.toString()
            val getBaldnessTxtInpEditTxt = baldnessTxtInpEditTxt.text.toString()
            val getHairTypeTxtInpEditTxt = hairTypeTxtInpEditTxt.text.toString()
            val getSurgPlanSuggTxtInpEditTxt = surgPlanSuggTxtInpEditTxt.text.toString()
            val getSurgPlanOptTxtInpEditTxt = surgPlanOptTxtInpEditTxt.text.toString()
            val getSurDtTiTxtInpEditTxt = surDtTiTxtInpEditTxt.text.toString()
           /* val getValue1TxtInpEditTxt = value1TxtInpEditTxt.text.toString()
            val getValue2TxtInpEditTxt = value2TxtInpEditTxt.text.toString()*/

            val patientPhotoFile = File(patientPhotoAbsolutePath)
            val beforeScalpPhotoFile = File(beforeScalpAbsolutePath)
//            val afterScalpPhotoFile = File(afterScalpAbsolutePath)

            if (getNameTxtInpEditTxt.isBlank()){
                //nameTxtInputLayout.setError("Patient Name Should not be blank")
                nameTxtInpEditTxt.setError("Patient Name Should not be blank")
                Toast.makeText(this@MainActivity, "Patient Name Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getAgeTxtInpEditTxt.isBlank()){
                ageTxtInpEditTxt.setError("Patient Age Should not be blank")
                Toast.makeText(this@MainActivity, "Patient Age Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getMbNoTxtInpEditTxt.isBlank()){
                mbNoTxtInpEditTxt.setError("Patient Mobile No Should not be blank")
                Toast.makeText(this@MainActivity, "Patient Mobile No Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getEmailTxtInpEditTxt.isBlank()){
                emailTxtInpEditTxt.setError("Patient Email-Id Should not be blank")
                Toast.makeText(this@MainActivity, "Patient Email-Id Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (patientPhotoAbsolutePath == "" || patientPhotoFile.length() == 0L){
                Toast.makeText(this@MainActivity, "Please capture Patient Profile Photo", Toast.LENGTH_SHORT).show()
            } else if (getDocNameTxtInpEditTxt.isBlank()){
                docNameTxtInpEditTxt.setError("Doctor Name Should not be blank")
                Toast.makeText(this@MainActivity, "Doctor Name Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getDocEmailTxtInpEditTxt.isBlank()){
                docEmailTxtInpEditTxt.setError("Doctor Email-Id Should not be blank")
                Toast.makeText(this@MainActivity, "Doctor Email-Id Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getBaldnessTxtInpEditTxt.isBlank()){
                baldnessTxtInpEditTxt.setError("Baldness Grade Should not be blank")
                Toast.makeText(this@MainActivity, "Baldness Grade Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getHairTypeTxtInpEditTxt.isBlank()){
                hairTypeTxtInpEditTxt.setError("Hair Type Should not be blank")
                Toast.makeText(this@MainActivity, "Hair Type Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getSurgPlanSuggTxtInpEditTxt.isBlank()){
                surgPlanSuggTxtInpEditTxt.setError("Surgical Plan Suggested Should not be blank")
                Toast.makeText(this@MainActivity, "Surgical Plan Suggested Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getSurgPlanOptTxtInpEditTxt.isBlank()){
                surgPlanOptTxtInpEditTxt.setError("Surgical Plan Opted Should not be blank")
                Toast.makeText(this@MainActivity, "Surgical Plan Opted Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getSurDtTiTxtInpEditTxt.isBlank()){
                surDtTiTxtInpEditTxt.setError("Surgery Date and Time Should not be blank")
                Toast.makeText(this@MainActivity, "Surgery Date and Time Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (beforeScalpAbsolutePath == "" || beforeScalpPhotoFile.length() == 0L){
                Toast.makeText(this@MainActivity, "Please capture Before Scalp Photo", Toast.LENGTH_SHORT).show()
            } /*else if (afterScalpAbsolutePath == "" || afterScalpPhotoFile.length() == 0.toLong()){
                Toast.makeText(this@MainActivity, "Please capture After Scalp Photo", Toast.LENGTH_SHORT).show()
            } else if (getValue1TxtInpEditTxt.isBlank()){
                value1TxtInpEditTxt.setError("Value 1 Should not be blank")
                Toast.makeText(this@MainActivity, "Value 1 Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getValue2TxtInpEditTxt.isBlank()){
                value2TxtInpEditTxt.setError("Value 2 Should not be blank")
                Toast.makeText(this@MainActivity, "Value 2 Should not be blank", Toast.LENGTH_SHORT).show()
            }*/ else{

                lifecycleScope.launch {

//                    var fileName1 = ""
//                    val document = Document()
//                    //fileName1 = "TEST" + ".pdf"
//
//                    var txtId: String = Random(System.currentTimeMillis()).nextInt(99999).toString()
//                    fileName1 = "$getNameTxtInpEditTxt$txtId.pdf"
//                    val dest: String = this@MainActivity.getExternalFilesDir(null).toString() + "/"
//                    val dir = File(dest)
//                    if (!dir.exists()) dir.mkdirs()
//
//                    try {
//                        val file = File(dest, fileName1)
//                        file.createNewFile()
//                        val fOut = FileOutputStream(file, false)
//                        val writer = PdfWriter.getInstance(document, fOut)
//
//                        document.open();
//
//                        val gender = radioButton.text.toString()
//                        document.add(Paragraph("Patient Name :- $getNameTxtInpEditTxt"))
//                        document.add(Paragraph("Patient Age  :- $getAgeTxtInpEditTxt"))
//                        document.add(Paragraph("Patient Gender :- $gender"))
//                        document.add(Paragraph("Patient Mobile Number :- $getMbNoTxtInpEditTxt"))
//                        document.add(Paragraph("Patient Email-Id :- $getEmailTxtInpEditTxt"))
//                        document.add(Paragraph("Doctor Name :- $getDocNameTxtInpEditTxt"))
//                        document.add(Paragraph("Doctor Email-Id :- $getDocEmailTxtInpEditTxt"))
//                        document.add(Paragraph("Baldness Grade :-  $getBaldnessTxtInpEditTxt"))
//                        document.add(Paragraph("Hair Type :- $getHairTypeTxtInpEditTxt"))
//                        document.add(Paragraph("Surgical Plan Suggested :- $getSurgPlanSuggTxtInpEditTxt"))
//                        document.add(Paragraph("Surgical Plan Opted $getSurgPlanOptTxtInpEditTxt"))
//                        document.add(Paragraph("Surgery Date and Time $getSurDtTiTxtInpEditTxt"))
//                        /*document.add(Paragraph("Value 1 :- $getValue1TxtInpEditTxt"))
//                        document.add(Paragraph("Value 2 :- $getValue2TxtInpEditTxt"))*/
//
//                        document.newPage()
//
//                        val patientPhotoFile = File(patientPhotoAbsolutePath)
//                        val beforeScalpPhotoFile = File(beforeScalpAbsolutePath)
//                        val afterScalpPhotoFile = File(afterScalpAbsolutePath)
//                        if (patientPhotoFile.length() > 0) {
//                            patientPhotoBitmap = BitmapFactory.decodeFile(patientPhotoAbsolutePath)
//                            patientImage = patientPhotoBitmap.let { convertBitmapToByteArray(it) }
//                            document.add(patientImage)
//                            document.newPage()
//                        } else {
//                            Log.v("patientPhotoFile", "patientPhotoFile is null or empty")
//                        }
//
//                        if (beforeScalpPhotoFile.length() > 0){
//                            beforeScalpBitmap = BitmapFactory.decodeFile(beforeScalpAbsolutePath)
//                            beforeScalpImage = beforeScalpBitmap.let { convertBitmapToByteArray(it) }
//                            document.add(beforeScalpImage)
//                            document.newPage()
//                        } else {
//                            Log.v("beforeScalpPhotoFile", "beforeScalpPhotoFile is null or empty")
//                        }
//
//                        if (afterScalpPhotoFile.length() > 0){
//                            afterScalpBitmap = BitmapFactory.decodeFile(afterScalpAbsolutePath)
//                            afterScalpImage = afterScalpBitmap.let { convertBitmapToByteArray(it) }
//                            document.add(afterScalpImage)
//                            document.newPage()
//                        } else {
//                            Log.v("afterScalpPhotoFile", "afterScalpPhotoFile is null or empty")
//                        }
//
//                        if (imagePickedScreenshotUri != null && !Uri.EMPTY.equals(imagePickedScreenshotUri)) {
//                            //doTheThing()
//                            Log.v("pickedScreenpUri", "Available")
//                            val ScreenshotBitmap = getBitmap(contentResolver, imagePickedScreenshotUri)
//                            screenshotImage = ScreenshotBitmap?.let { convertBitmapToByteArray(it) }
//                            document.add(screenshotImage)
//                            document.newPage()
//                        } else {
//                            Log.v("pickedScreenpUri", "is null or empty")
//                        }
//
//                        /* if (imagePickedScreenshotUri != null){
//                             val ScreenshotBitmap = getBitmap(contentResolver, imagePickedScreenshotUri)
//                             screenshotImage = ScreenshotBitmap?.let { convertBitmapToByteArray(it) }
//                             document.add(screenshotImage)
//                             document.newPage()
//                         }*/
//
//                        document.close();
//
//                    } catch (e: DocumentException) {
//                        e.printStackTrace()
//                        Log.v("PdfError", e.toString())
//                    } catch (e: FileNotFoundException) {
//                        e.printStackTrace()
//                        Log.v("PdfError", e.toString())
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                        Log.v("PdfError", e.toString())
//                    }

                    /*var pdfFilePath = "$dest/$fileName1"
                    Log.v("pdfFilePath", pdfFilePath)

                    val pdfFile = File("$dest/$fileName1")
                    if (!pdfFile.exists()) {
                        pdfFile.mkdir()
                    }
                    if (pdfFile != null && pdfFile.exists()) //Checking for the file is exist or not
                    {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val mURI: Uri = FileProvider.getUriForFile(
                            this@MainActivity, this@MainActivity.getApplicationContext()
                                .getPackageName().toString() + ".provider", pdfFile
                        )
                        intent.setDataAndType(mURI, "application/pdf")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        try {
                            this@MainActivity.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "The file not exists! ", Toast.LENGTH_SHORT)
                            .show()
                    }*/

                    val patientDataItem = PatientDataEntity(0,"",
                        getNameTxtInpEditTxt ?: "", getAgeTxtInpEditTxt.toInt() ?: 0, radioButton.text.toString() ?: "",
                        getMbNoTxtInpEditTxt ?: "", getEmailTxtInpEditTxt ?: "",patientPhotoAbsolutePath ?: "",
                        getDocNameTxtInpEditTxt ?: "", getDocEmailTxtInpEditTxt ?: "", getBaldnessTxtInpEditTxt ?: "",
                        getHairTypeTxtInpEditTxt ?: "",getSurgPlanSuggTxtInpEditTxt ?: "",
                        getSurgPlanOptTxtInpEditTxt ?: "",beforeScalpAbsolutePath ?: "",
                        "", getSurDtTiTxtInpEditTxt ?: "",
                        "", "", "", "")

                    patientAppDB?.insert(patientDataItem)

                    nameTxtInpEditTxt.setText("")
                    ageTxtInpEditTxt.setText("")

                    mbNoTxtInpEditTxt.setText("")
                    emailTxtInpEditTxt.setText("")
                    docNameTxtInpEditTxt.setText("")
                    docEmailTxtInpEditTxt.setText("")
                    baldnessTxtInpEditTxt.setText("")
                    hairTypeTxtInpEditTxt.setText("")
                    surgPlanSuggTxtInpEditTxt.setText("")
                    surgPlanOptTxtInpEditTxt.setText("")
                    surDtTiTxtInpEditTxt.setText("")
                    /*value1TxtInpEditTxt.setText("")
                    value2TxtInpEditTxt.setText("")*/

                    setProfilePhotoImageView.setImageURI(null)
                    setProfilePhotoImageView.visibility = View.GONE
                    setBeforeScalpImageView.setImageURI(null)
                    setBeforeScalpImageView.visibility = View.GONE
                    /*setAfterScalpImageView.setImageURI(null)
                    setAfterScalpImageView.visibility = View.GONE
                    setUploadScreenshotImageView.setImageURI(null)
                    setUploadScreenshotImageView.visibility = View.GONE*/

                    /*val blynkAppMarketLink = "market://details?id=cloud.blynk"

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(blynkAppMarketLink))
                    startActivity(intent)*/

                    startNewApplication(this@MainActivity, "cloud.blynk")
//        openApp(this@MainActivity, "Blynk IoT", "cloud.blynk")

                    /*val blynkAppMarketLink = "market://details?id=cloud.blynk"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(blynkAppMarketLink))
                    startActivity(intent)*/

                    finish()
                }
            }
        }

        /*val bitmap1 = BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.patientdataentry/files/Pictures/IMG_9019131565039713377.jpg")
        setProfilePhotoImageView.visibility = View.VISIBLE
        setProfilePhotoImageView.setImageBitmap(bitmap1)

        val bitmap2 = BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.patientdataentry/files/Pictures/IMG_3269560358304437075.jpg")
        setBeforeScalpImageView.visibility = View.VISIBLE
        setBeforeScalpImageView.setImageBitmap(bitmap2)

        val bitmap3 = BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.example.patientdataentry/files/Pictures/IMG_5736689874160266018.jpg")
        setAfterScalpImageView.visibility = View.VISIBLE
        setAfterScalpImageView.setImageBitmap(bitmap3)

        setUploadScreenshotImageView.visibility = View.VISIBLE
        setUploadScreenshotImageView.setImageURI(Uri.parse("content://media/external/images/media/641"))*/
    }

    private val takePatientPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            Log.e("abx", "aserfd $isSaved")
            /*cropImageView.visibility = View.VISIBLE
            cropImageView.setImageUriAsync(patientPhotoUri)*/

            setProfilePhotoImageView.visibility = View.VISIBLE
            setProfilePhotoImageView.setImageURI(patientPhotoUri)
        }
    }

    private val takepatientBeforeScalpPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            Log.e("abx", "aserfd $isSaved")
            setBeforeScalpImageView.visibility = View.VISIBLE
            setBeforeScalpImageView.setImageURI(patientBeforeScalpPhotoUri)
        }
    }

    /*private val takepatientAterScalpPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            Log.e("abx", "aserfd $isSaved")
            setAfterScalpImageView.visibility = View.VISIBLE
            setAfterScalpImageView.setImageURI(patientAfterScalpPhotoUri)
        }
    }*/

   /* var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uriResult ->
            uriResult?.let {
                Log.e("pickImage", "Image picked " + uriResult)
                imagePickedScreenshotUri = uriResult
                Log.e("pickImage", "Image picked " + imagePickedScreenshotUri.toString())
                setUploadScreenshotImageView.setImageURI(imagePickedScreenshotUri)
                //setCrooppedImageView.setImageURI(Uri.parse("content://media/external/images/media/80831"))
                //set this URI to imageview then get bitmap or get directly bitmap store in PDF
            }
        }*/

    private lateinit var byteArray: ByteArray
    private fun convertBitmapToByteArray(bitmap: Bitmap): Image {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        byteArray = stream.toByteArray()
        byteArray = byteArray.copyOf(256)

        val image = Image.getInstance(stream.toByteArray())
        image.scalePercent(22F)
        image.alignment = Element.ALIGN_LEFT

        return image
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
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takePatientPhotoPicture.launch(patientPhotoUri)

                /*cropImageView.setImageBitmap(null)
                setCropImageView.setImageBitmap(null)*/
                //}
            }
            return
        }
        if (requestCode == BEFORE_SCALP_PERMISSION__REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takepatientBeforeScalpPhotoPicture.launch(patientBeforeScalpPhotoUri)

                /*cropImageView.setImageBitmap(null)
                setCropImageView.setImageBitmap(null)*/
                //}
            }
            return
        }
        /*if (requestCode == AFTER_SCALP_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takepatientAterScalpPhotoPicture.launch(patientAfterScalpPhotoUri)

            }
            return
        }*/
    }



    fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception){
            null
        }
    }

    /* val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }
    }*/

    /*requestMultiplePermissions.launch(
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )*/

    /*val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // PERMISSION GRANTED
        } else {
            // PERMISSION NOT GRANTED
        }
    }

    // Ex. Launching ACCESS_FINE_LOCATION permission.
    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }*/

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
       /* val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")*/
        return sdf.format(Date())
    }


    fun startNewActivity(context: Context, packageName: String) {
        var intent: Intent? = context.getPackageManager().getLaunchIntentForPackage(packageName)
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun startNewApplication(context: Context, packageName: String) {
        var intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(intent)
        }
    }

    fun openApp(context: Context, appName: String, packageName: String?) {
        if (isAppInstalled(context, packageName!!))
            if (isAppEnabled(context, packageName))
                context.startActivity(context.packageManager.getLaunchIntentForPackage(packageName))
            else
                Toast.makeText(context, "$appName app is not enabled.", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "$appName app is not installed.", Toast.LENGTH_SHORT).show()
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
        return false
    }

    private fun isAppEnabled(context: Context, packageName: String): Boolean {
        var appStatus = false
        try {
            val ai = context.packageManager.getApplicationInfo(packageName, 0)
            if (ai != null) {
                appStatus = ai.enabled
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appStatus
    }

    /*val patientDataItem = PatientDataEntity(0,"",
        getNameTxtInpEditTxt ?: "", getAgeTxtInpEditTxt.toInt() ?: 0, radioButton.text.toString() ?: "",
        getMbNoTxtInpEditTxt ?: "", getEmailTxtInpEditTxt ?: "",patientPhotoAbsolutePath ?: "",
        getDocNameTxtInpEditTxt ?: "", getDocEmailTxtInpEditTxt ?: "", getBaldnessTxtInpEditTxt ?: "",
        getHairTypeTxtInpEditTxt ?: "",getSurgPlanSuggTxtInpEditTxt ?: "",
        getSurgPlanOptTxtInpEditTxt ?: "",beforeScalpAbsolutePath ?: "",
        afterScalpAbsolutePath ?: "", getSurDtTiTxtInpEditTxt ?: "",
        getValue1TxtInpEditTxt ?: "", getValue2TxtInpEditTxt ?: "",
        imagePickedScreenshotUri.toString()?: "", pdfFilePath ?: "")*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_getlist -> {
                val intent = Intent(this@MainActivity,GetPatientListData::class.java)
                startActivity(intent)
            }
            R.id.action_systemconfig -> Toast.makeText(this,"Sytem Config Selected",Toast.LENGTH_SHORT).show()
            R.id.action_about -> Toast.makeText(this,"About Selected",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}