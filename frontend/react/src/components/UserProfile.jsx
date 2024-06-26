const UserProfile = (props) => {
    return (
        <div>
            <p>{props.name}</p>
            <p>{props.age}</p>
            <img src={`https://randomuser.me/api/portraits/thumb/${props.gender}/73.jpg`}></img>
        </div>
    )
}

export default UserProfile