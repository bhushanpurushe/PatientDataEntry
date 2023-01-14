package com.example.patientdataentry

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageView
import com.example.patientdataentry.database.db.PatientAppDB
import com.example.patientdataentry.database.entity.PatientDataEntity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.text.*
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mParentConstraintLayout : ConstraintLayout
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
    private lateinit var saveButton : Button
    private lateinit var openBlynkAppButton : Button
    private lateinit var openHotspotButton : Button
    private lateinit var patientPhotoButton : Button
    private lateinit var patientScalpBeforeButton : Button
    private lateinit var patientLeftScalpBeforeButton : Button
    private lateinit var patientRightScalpBeforeButton : Button
    private lateinit var setProfilePhotoImageView: ImageView
    private lateinit var setBeforeScalpImageView: ImageView
    private lateinit var setBeforeLeftScalpImageView: ImageView
    private lateinit var setBeforeRightScalpImageView: ImageView

    private var patientPhotoAbsolutePath: String? = ""
    private var beforeScalpAbsolutePath: String? = ""
    private var beforeLeftScalpAbsolutePath: String? = ""
    private var beforeRightScalpAbsolutePath: String? = ""

    private var patientPhotoUri : Uri? = null
    private var patientBeforeScalpPhotoUri : Uri? = null
    private var patientBeforeLeftScalpPhotoUri : Uri? = null
    private var patientBeforeRightScalpPhotoUri : Uri? = null

    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 44
    private val BEFORE_SCALP_PERMISSION__REQUEST_CODE = 45
    private val BEFORE_LEFT_SCALP_PERMISSION__REQUEST_CODE = 46
    private val BEFORE_RIGHT_SCALP_PERMISSION__REQUEST_CODE = 47
    private val AFTER_SCALP_PERMISSION_REQUEST_CODE = 48
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE")

    private val patientAppDB by lazy { PatientAppDB.getAppDb(this)?.patientdao() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mParentConstraintLayout = findViewById(R.id.parentConstraintLayout)
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

        /*nameTxtInpEditTxt.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO)
        emailTxtInpEditTxt.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO)
        docEmailTxtInpEditTxt.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO)*/

        saveButton = findViewById(R.id.save_button)
        openBlynkAppButton = findViewById(R.id.open_blynkapp_button)
        openHotspotButton = findViewById(R.id.open_hotspt_button)
        patientPhotoButton = findViewById(R.id.patient_photo_button)
        patientScalpBeforeButton = findViewById(R.id.scalp_before_photo_button)
        patientLeftScalpBeforeButton = findViewById(R.id.left_scalp_before_photo_button)
        patientRightScalpBeforeButton = findViewById(R.id.right_scalp_before_photo_button)

        setProfilePhotoImageView = findViewById(R.id.profile_imageView)
        setBeforeScalpImageView = findViewById(R.id.before_scalp_imageView)
        setBeforeLeftScalpImageView = findViewById(R.id.before_left_scalp_imageView)
        setBeforeRightScalpImageView = findViewById(R.id.before_right_scalp_imageView)

        val date = getCurrentDate()
        System.out.println(" today date is - $date")
        surDtTiTxtInpEditTxt.setText(date)

        patientPhotoButton.setOnClickListener {

            val photoFile = File.createTempFile("IMG_", ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

            patientPhotoUri = FileProvider.getUriForFile(applicationContext,
                "${applicationContext.packageName}.provider", photoFile)

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
        }

        patientScalpBeforeButton.setOnClickListener {

            val photoBeforeScalpFile = File.createTempFile("IMG_", ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

            patientBeforeScalpPhotoUri = FileProvider.getUriForFile(applicationContext,
                "${applicationContext.packageName}.provider", photoBeforeScalpFile)

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
                takepatientBeforeScalpPhotoPicture.launch(patientBeforeScalpPhotoUri)
            }
        }

        patientLeftScalpBeforeButton.setOnClickListener {

            val photoBeforeLeftScalpFile = File.createTempFile("IMG_", ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

            patientBeforeLeftScalpPhotoUri = FileProvider.getUriForFile(applicationContext,
                "${applicationContext.packageName}.provider", photoBeforeLeftScalpFile)

            beforeLeftScalpAbsolutePath = photoBeforeLeftScalpFile.absolutePath
            Log.e("photo-absolutePath", beforeScalpAbsolutePath.toString())

            val name : String = photoBeforeLeftScalpFile.name
            Log.e("photo-name", name)
            val nameWithoutExtension : String = photoBeforeLeftScalpFile.nameWithoutExtension
            Log.e("photo-nameWithoutExt", nameWithoutExtension)
            val path : String = photoBeforeLeftScalpFile.path
            Log.e("photo-path", path)

            val isAllowPermission = allPermissionsGranted()
            Log.e("abx", "Permission Granted $isAllowPermission")
            if (!isAllowPermission){
                requestPermissions(REQUIRED_PERMISSIONS, BEFORE_LEFT_SCALP_PERMISSION__REQUEST_CODE)
            }else{
                Log.e("abx", "Already Permission Granted ")
                takepatientBeforeLeftScalpPhotoPicture.launch(patientBeforeLeftScalpPhotoUri)
            }
        }

        patientRightScalpBeforeButton.setOnClickListener {

            val photoBeforeRightScalpFile = File.createTempFile("IMG_", ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

            patientBeforeRightScalpPhotoUri = FileProvider.getUriForFile(applicationContext,
                "${applicationContext.packageName}.provider", photoBeforeRightScalpFile)

            beforeRightScalpAbsolutePath = photoBeforeRightScalpFile.absolutePath
            Log.e("photo-absolutePath", beforeScalpAbsolutePath.toString())

            val name : String = photoBeforeRightScalpFile.name
            Log.e("photo-name", name)
            val nameWithoutExtension : String = photoBeforeRightScalpFile.nameWithoutExtension
            Log.e("photo-nameWithoutExt", nameWithoutExtension)
            val path : String = photoBeforeRightScalpFile.path
            Log.e("photo-path", path)

            val isAllowPermission = allPermissionsGranted()
            Log.e("abx", "Permission Granted $isAllowPermission")
            if (!isAllowPermission){
                requestPermissions(REQUIRED_PERMISSIONS, BEFORE_RIGHT_SCALP_PERMISSION__REQUEST_CODE)
            }else{
                Log.e("abx", "Already Permission Granted ")
                takepatientBeforeRightScalpPhotoPicture.launch(patientBeforeRightScalpPhotoUri)
            }
        }

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

            val patientPhotoFile = File(patientPhotoAbsolutePath)
            val beforeScalpPhotoFile = File(beforeScalpAbsolutePath)
            val beforeLeftScalpPhotoFile = File(beforeLeftScalpAbsolutePath)
            val beforeRightScalpPhotoFile = File(beforeRightScalpAbsolutePath)

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
            } /*else if (getHairTypeTxtInpEditTxt.isBlank()){
                hairTypeTxtInpEditTxt.setError("Hair Type Should not be blank")
                Toast.makeText(this@MainActivity, "Hair Type Should not be blank", Toast.LENGTH_SHORT).show()
            }*/ else if (getSurgPlanSuggTxtInpEditTxt.isBlank()){
                surgPlanSuggTxtInpEditTxt.setError("Surgical Plan Suggested Should not be blank")
                Toast.makeText(this@MainActivity, "Surgical Plan Suggested Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getSurgPlanOptTxtInpEditTxt.isBlank()){
                surgPlanOptTxtInpEditTxt.setError("Surgical Plan Opted Should not be blank")
                Toast.makeText(this@MainActivity, "Surgical Plan Opted Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getSurDtTiTxtInpEditTxt.isBlank()){
                surDtTiTxtInpEditTxt.setError("Surgery Date and Time Should not be blank")
                Toast.makeText(this@MainActivity, "Surgery Date and Time Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (beforeScalpAbsolutePath == "" || beforeScalpPhotoFile.length() == 0L){
                Toast.makeText(this@MainActivity, "Please capture Front Scalp Photo", Toast.LENGTH_SHORT).show()
            } /*else if (beforeLeftScalpAbsolutePath == "" || beforeLeftScalpPhotoFile.length() == 0L){
                Toast.makeText(this@MainActivity, "Please capture Left Scalp Photo", Toast.LENGTH_SHORT).show()
            } else if (beforeRightScalpAbsolutePath == "" || beforeRightScalpPhotoFile.length() == 0L){
                Toast.makeText(this@MainActivity, "Please capture Right Scalp Photo", Toast.LENGTH_SHORT).show()
            }*/ else{

                lifecycleScope.launch {

                    try {

                        val patientDataItem = PatientDataEntity(0,"",
                            getNameTxtInpEditTxt ?: "", getAgeTxtInpEditTxt.toInt() ?: 0, radioButton.text.toString() ?: "",
                            getMbNoTxtInpEditTxt ?: "", getEmailTxtInpEditTxt ?: "",patientPhotoAbsolutePath ?: "",
                            getDocNameTxtInpEditTxt ?: "", getDocEmailTxtInpEditTxt ?: "", getBaldnessTxtInpEditTxt ?: "",
                            getHairTypeTxtInpEditTxt ?: "",getSurgPlanSuggTxtInpEditTxt ?: "",
                            getSurgPlanOptTxtInpEditTxt ?: "",beforeScalpAbsolutePath ?: "",
                            beforeLeftScalpAbsolutePath?: "", beforeRightScalpAbsolutePath?: "",
                            "",getSurDtTiTxtInpEditTxt ?: "", "",
                            "", "", "","")

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

                        setProfilePhotoImageView.setImageURI(null)
                        setProfilePhotoImageView.visibility = View.GONE
                        setBeforeScalpImageView.setImageURI(null)
                        setBeforeScalpImageView.visibility = View.GONE

                        saveButton.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        saveButton.isEnabled = false
                        openBlynkAppButton.visibility = View.VISIBLE
                        openHotspotButton.visibility = View.VISIBLE

                    }catch (e:Exception){
                        Toast.makeText(this@MainActivity,"Opps something went wrong!! ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        openHotspotButton.setOnClickListener {
            activeTethering()
        }

        openBlynkAppButton.setOnClickListener {
            if (isNetworkAvailable(this@MainActivity)){
                startNewApplication(this@MainActivity, "cloud.blynk")
                finish()
            }else{
                displayInternetSnackBar(mParentConstraintLayout)
            }
        }
    }

    private val takePatientPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            Log.e("abx", "aserfd $isSaved")
            /*setProfilePhotoImageView.visibility = View.VISIBLE
            setProfilePhotoImageView.setImageURI(patientBeforeScalpPhotoUri)*/
            invokeAltertDialogForCropping(patientPhotoUri, setProfilePhotoImageView, "PatientProfilePhoto")
        }
    }

    private val takepatientBeforeScalpPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            Log.e("abx", "aserfd $isSaved")
           /* setBeforeScalpImageView.visibility = View.VISIBLE
            setBeforeScalpImageView.setImageURI(patientBeforeScalpPhotoUri)*/
            invokeAltertDialogForCropping(patientBeforeScalpPhotoUri, setBeforeScalpImageView, "PatientBeforeScalpPhoto")
        }
    }

    private val takepatientBeforeLeftScalpPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            invokeAltertDialogForCropping(patientBeforeLeftScalpPhotoUri, setBeforeLeftScalpImageView, "PatientBeforeLeftScalpPhoto")
        }
    }

    private val takepatientBeforeRightScalpPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            invokeAltertDialogForCropping(patientBeforeRightScalpPhotoUri, setBeforeRightScalpImageView, "PatientBeforeRightScalpPhoto")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_getlist -> {
                val intent = Intent(this@MainActivity,GetPatientListData::class.java)
                startActivity(intent)
            }
           /* R.id.action_systemconfig -> Toast.makeText(this,"Sytem Config Selected",Toast.LENGTH_SHORT).show()
            R.id.action_about -> Toast.makeText(this,"About Selected",Toast.LENGTH_SHORT).show()*/
        }
        return super.onOptionsItemSelected(item)
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
                //}
            }
            return
        }
        if (requestCode == BEFORE_SCALP_PERMISSION__REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takepatientBeforeScalpPhotoPicture.launch(patientBeforeScalpPhotoUri)
                //}
            }
            return
        }
        if (requestCode == BEFORE_LEFT_SCALP_PERMISSION__REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takepatientBeforeLeftScalpPhotoPicture.launch(patientBeforeLeftScalpPhotoUri)
                //}
            }
            return
        }
        if (requestCode == BEFORE_RIGHT_SCALP_PERMISSION__REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takepatientBeforeRightScalpPhotoPicture.launch(patientBeforeRightScalpPhotoUri)
                //}
            }
            return
        }
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aaa")
        return sdf.format(Date())
    }

    fun startNewApplication(context: Context, packageName: String) {

        try {
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
        }catch (e:Exception){
            Toast.makeText(this@MainActivity,"Issue opening with cloud.blynk App ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun invokeAltertDialogForCropping(imageUri: Uri?, imageView: ImageView, imagePointsTo : String) {

        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alter_dialog)
        dialog.getWindow()?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setTitle("Crop Image")
        dialog.setCancelable(false)

        val cropImage = dialog.findViewById(R.id.dialog_cropImageView) as CropImageView
        val cropButton = dialog.findViewById(R.id.dialog_crop_image_button) as Button

        cropImage.setImageUriAsync(imageUri)

        cropButton.setOnClickListener {
            val mCroppedPhotoBitmap : Bitmap? = cropImage.croppedImage

            imageView.visibility = View.VISIBLE
            imageView.setImageBitmap(mCroppedPhotoBitmap)

            val getProfilePhotoPath : String? = getAbsolutePathOfBitmapFile(mCroppedPhotoBitmap)
            Log.e("ImageCheck", "Profile Photo $getProfilePhotoPath")

            when(imagePointsTo){
                "PatientProfilePhoto" -> {
                    Log.e("ImageCheck", "x == previous $patientPhotoAbsolutePath")
                    patientPhotoAbsolutePath = getProfilePhotoPath
                    Log.e("ImageCheck", "x == Now $patientPhotoAbsolutePath")
                }
                "PatientBeforeScalpPhoto" -> {
                    Log.e("ImageCheck", "x == previous $beforeScalpAbsolutePath")
                    beforeScalpAbsolutePath = getProfilePhotoPath
                    Log.e("ImageCheck", "x == Now $beforeScalpAbsolutePath")
                }
                "PatientBeforeLeftScalpPhoto" -> {
                    Log.e("ImageCheck", "x == previous $beforeLeftScalpAbsolutePath")
                    beforeLeftScalpAbsolutePath = getProfilePhotoPath
                    Log.e("ImageCheck", "x == Now $beforeLeftScalpAbsolutePath")
                }
                "PatientBeforeRightScalpPhoto" -> {
                    Log.e("ImageCheck", "x == previous $beforeRightScalpAbsolutePath")
                    beforeRightScalpAbsolutePath = getProfilePhotoPath
                    Log.e("ImageCheck", "x == Now $beforeRightScalpAbsolutePath")
                }
                else -> {
                    Log.e("ImageCheck", "x is neither 1 nor 2")
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getAbsolutePathOfBitmapFile(mCroppedPhotoBitmap: Bitmap?): String? {

        var tempFileAbsolutePath: String? = ""

        var bitmapTempFile = File.createTempFile("IMG_", ".jpg",
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

        var tempPhotoUri : Uri? = FileProvider.getUriForFile(applicationContext,
            "${applicationContext.packageName}.provider", bitmapTempFile)

        tempFileAbsolutePath = bitmapTempFile.absolutePath
        Log.e("photo-absolutePath", tempFileAbsolutePath.toString())

        val name: String = bitmapTempFile.name
        Log.e("photo-name", name)

        val tempFile = File.createTempFile("IMG_", ".jpg")
        val bytes = ByteArrayOutputStream()
        mCroppedPhotoBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(bitmapTempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()

        // if you want to return uri then use below line of code
        //return Uri.fromFile(tempFile) & return method as Uri

        return tempFileAbsolutePath
    }

    private fun activeTethering() {
        val tetherSettings = Intent()
        tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings")
        startActivity(tetherSettings)
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun displayInternetSnackBar(view: View){
        //Snackbar(view)
        val snackbar = Snackbar.make(view, "Oops! Seems you don’t have working internet connection", Snackbar.LENGTH_LONG).setAction("Action", null)
        snackbar.setActionTextColor(Color.RED)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.RED)
        val textView = snackbarView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 14f
        snackbar.show()
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
}