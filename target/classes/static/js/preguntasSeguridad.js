
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
        
     var validarTerminos = function(evento){
        if (formulario.terminos.checked == true) {
        }else{
            alerta.innerHTML = '<div class="alert alert-danger"><a href="" class="close" data-dismiss="alert">&times;</a>Debe leer y aceptar los terminos</div>';
            evento.preventDefault();
            formulario.terminos.focus();
            formulario.terminos.select();
        }
    };
    
    var validar = function(evento){
        validarTerminos(evento);
    };
        
    formulario.addEventListener('submit',validar);
  
})();
