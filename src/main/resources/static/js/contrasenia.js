
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
        
    var validarPass = function(evento){
        if (formulario.password.value !== formulario.confirmPassword.value) {
            alerta.innerHTML = '<div class="alert alert-danger"><a href="" class="close" data-dismiss="alert">&times;</a>Las contraseñas no coinciden</div>';
            evento.preventDefault();
        }
    };
    
    var validar = function(evento){
    	validarPass(evento);
    };
        
    formulario.addEventListener('submit',validar);
  
})();
