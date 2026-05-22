def test_api_logic():
    response = {"status": "ok"}
    assert response["status"] == "ok"

def test_addition():
    assert 2 + 2 == 4


def test_string():
    assert "devops".upper() == "DEVOPS"