package com.example.gtm.data.entities.response.mytaskplanning.detailservicequestionnaire.compterendu

import com.example.gtm.data.entities.remote.ImagePath
import com.example.gtm.data.entities.ui.Image

data class PostSubjectCompteRendu(
    val visitId : Int = -1,
    val reportText : String? =null,
    val pictures : List<ImagePath>? = null,
    val reportSubjects: List<PostSubject>? =null,
    val Images : List<Image>? =null

)
