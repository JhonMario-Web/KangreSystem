
// Example starter JavaScript for disabling form submissions if there are invalid fields
(function() {
  'use strict';
  window.addEventListener('load', function() {
    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    var forms = document.getElementsByClassName('needs-validation');
    // Loop over them and prevent submission
    var validation = Array.prototype.filter.call(forms, function(form) {
      form.addEventListener('submit', function(event) {
        if (form.checkValidity() === false) {
          event.preventDefault();
          event.stopPropagation();
        }
        form.classList.add('was-validated');
      }, false);
    });
  }, false);
  
  	var formulario = document.getElementsByName('formRegistro')[0],
        alerta = document.getElementById('alerta');
        
    var validarCorreos = function(evento){
        if (formulario.email.value !== formulario.confirmEmail.value) {
            alerta.innerHTML = '<div class="alert alert-danger"><a href="" class="close" data-dismiss="alert">&times;</a>Los campos correo electronico y confirmar correo electronico no coinciden</div>';
            evento.preventDefault();
        }
    };
  
  	var validarGenero = function(evento){
        if (formulario.genero[0].checked == true || formulario.genero[1].checked == true) {
        }else{
            alerta.innerHTML = '<div class="alert alert-danger"><a href="" class="close" data-dismiss="alert">&times;</a>Completa el campo Genero</div>';
            evento.preventDefault();
            formulario.genero[0].focus();
            formulario.genero[0].select();
        }
    };
    
    var validar = function(evento){
        validarGenero(evento);
        validarCorreos(evento);
    };
        
    formulario.addEventListener('submit',validar);
  
})();




