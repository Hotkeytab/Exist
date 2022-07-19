package com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu

import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.ui.Image

data class SubjectCompteRendu(
    val visitId : Int,
    val reportText : String,
    val pictures : List<ImagePath>,
    val data: List<Subject>,
    val Images : List<Image>

    )




