package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewQuoteViewModel @Inject constructor(
    val useCase: AddQuoteUseCase
) : ViewModel(){

    fun addQuote(){
        viewModelScope.launch {

        }
    }
}