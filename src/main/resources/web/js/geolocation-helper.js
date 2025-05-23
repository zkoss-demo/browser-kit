
function requestLocation(){
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(success, error);
    } else {
        fireEventToServer({error: "Geolocation is not supported by this browser."});
    }
}

/**
 * there should be a Java event listener for this component in Java controller to receive the event
 */
function getAnchorWidget(){
    return zk.Widget.$('.geolocation-helper');
}

const GET_LOCATION_EVENT = 'onGetLocation';

function success(position) {
    fireEventToServer(position.toJSON());
}

function error(error) {
    fireEventToServer(error.toJSON());
}

function fireEventToServer(data){
    getAnchorWidget().fire(GET_LOCATION_EVENT, data, {toServer: true});
}