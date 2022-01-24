package com.sapmedicines.sapmedicines

class test {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == myChromeClient.mUploadMessage && null == myChromeClient.mUploadMessageAboveL) return
            val result = if (resultCode != RESULT_OK) null
            else if (data!=null) data.data
            else myChromeClient.mCapturedImageURI
            if (myChromeClient.mUploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (myChromeClient.mUploadMessage != null) {
                myChromeClient.mUploadMessage?.onReceiveValue(result)
                myChromeClient.mUploadMessage = null
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILECHOOSER_RESULTCODE || myChromeClient.mUploadMessageAboveL == null)
            return
        var results: Array<Uri>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = Array<Uri>(clipData.itemCount) { _->return Unit}
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        results[i] = item.uri
                    }
                }
                if (dataString != null)
                    results = arrayOf(Uri.parse(dataString))
            }else if (myChromeClient.mCapturedImageURI!=null)
                results = arrayOf(myChromeClient.mCapturedImageURI)
        }
        myChromeClient.mUploadMessageAboveL?.onReceiveValue(results)
        myChromeClient.mUploadMessageAboveL = null
    }
}